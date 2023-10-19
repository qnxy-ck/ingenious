package org.ingenious.parser;

import org.ingenious.Pair;
import org.ingenious.ast.*;
import org.ingenious.lexer.Lexer;
import org.ingenious.token.*;
import org.ingenious.token.keyword.*;
import org.ingenious.token.symbol.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 语法解析器-构建AST
 *
 * @author Qnxy
 */
public final class Parser extends CommonParser {

    public Parser(Lexer lexer) {
        super(lexer);
    }

    public Program parse() {
        return this.program();
    }

    private Program program() {
        return new Program(this.statementList());

    }

    /*
        StatementList
            : NamespaceStatement
            | ImportEntityStatement
            | OptImportStatementList
            | FunStatementList
            ;
     */
    private List<ASTree> statementList() {
        var list = new ArrayList<ASTree>();

        list.add(this.namespaceStatement());
        list.add(this.entityStatement());

        // OptImportStatementList
        if (this.test(ImportToken.class)) {
            list.add(this.importStatementList());
        } else {
            // 没有 import 则使用空的, 进行占位方便后面代码生成判断是否为类的开始
            list.add(ImportStatementList.EMPTY_IMPORT_STATEMENT_LIST);
        }

        list.addAll(this.funStatementList());
        return list;
    }

    /*
        FunStatementList
            : FunStatement
            | FunStatementList FunStatement
            ;
     */
    private List<ASTree> funStatementList() {
        final var list = new ArrayList<ASTree>();
        list.add(this.funStatement());

        while (this.lookahead() != null) {
            list.add(this.funStatement());
        }
        return list;
    }

    /*
        FunStatement
            : SearchFunDeclaration
            ;
     */
    private ASTree funStatement() {
        final var lookahead = this.lookahead();

        if (lookahead instanceof SearchToken) {
            return this.searchFunDeclaration();
        }

        throw new SyntaxException("(funStatement) Unexpected token: " + lookahead + " Expected token: [search]", lookahead);
    }

    /*
        SearchFunDeclaration
            : 'search' Identifier FormalParameters OptSearchReturnDeclaration SearchFunBlockStatement
            ;
     */
    private SearchFunDeclaration searchFunDeclaration() {
        this.consume(SearchToken.class);

        // Identifier
        IdentifierToken identifierToken = this.consume(IdentifierToken.class);

        // FormalParameters
        final var formalParameters = this.formalParameters();

        // OptSearchReturnDeclaration
        final var returnType = this.test(OpenCurlyBracketToken.class)
                ? FunTypeDeclaration.DEFAULT
                : this.searchReturnDeclaration();

        // SearchFunBlockStatement
        final var body = this.searchFunBlockStatement();

        return new SearchFunDeclaration(
                identifierToken.value(),
                formalParameters,
                returnType,
                body
        );
    }

    /*
        FormalParameters
            : '(' OptFormalParameterList ')'
            ;
     */
    private List<FormalParameter> formalParameters() {
        this.consume(OpenParenthesisToken.class);

        // OptFormalParameterList
        final var list = this.test(ClosedParenthesisToken.class)
                ? List.<FormalParameter>of()
                : this.formalParameterList();

        this.consume(ClosedParenthesisToken.class);
        return list;
    }

    /*
        FormalParameterList
            : FormalParameter
            | FormalParameterList ',' FormalParameter
            ;
     */
    private List<FormalParameter> formalParameterList() {
        final var list = new ArrayList<FormalParameter>();
        list.add(this.formalParameter());

        while (this.test(CommaToken.class)) {
            this.consume(CommaToken.class);
            list.add(this.formalParameter());
        }

        return list;
    }

    /*
        FormalParameter
            : NewLines Identifier ':' SearchFunTypeDeclaration NewLines
            ;
     */
    private FormalParameter formalParameter() {
        this.ignoreNewLines();
        IdentifierToken parameterName = this.consume(IdentifierToken.class);
        this.consume(ColonToken.class);

        final var parameterType = this.searchFunTypeDeclaration();

        this.ignoreNewLines();
        return new FormalParameter(parameterName.value(), parameterType);
    }

    /*
        SearchFunTypeDeclaration
            : OptMul TypeValue NewLines
            ;
     */
    private FunTypeDeclaration searchFunTypeDeclaration() {
        final var multiple = this.test(MultiToken.class);

        // OptMul
        if (multiple) {
            this.consume(MultiToken.class);
        }

        final var typeValue = this.namespacePath();
        this.ignoreNewLines();
        return new FunTypeDeclaration(multiple, typeValue);
    }

    /*
        SearchReturnDeclaration
            : ':' TypeValue
            | ':' 'multi' TypeValue
            ;
     */
    private FunTypeDeclaration searchReturnDeclaration() {
        this.consume(ColonToken.class);
        final var multiple = this.test(MultiToken.class);

        if (multiple) {
            this.consume(MultiToken.class);
        }

        if (this.test(OpenCurlyBracketToken.class)) {
            return FunTypeDeclaration.MUL_DEFAULT;
        }

        final var typeValue = this.namespacePath();
        return new FunTypeDeclaration(multiple, typeValue);
    }

    /*
        SearchFunBlockStatement
            : '{' NewLines SearchFunStatementList '}' NewLines
            ;
     */
    private SearchFunBlockStatement searchFunBlockStatement() {
        this.consume(OpenCurlyBracketToken.class);
        this.ignoreNewLines();

        final var body = this.searchFunStatementList();
        this.consume(ClosedCurlyBracketToken.class);
        this.ignoreNewLines();

        return new SearchFunBlockStatement(body);
    }

    /*  
        SearchFunStatementList
            : SearchFunStatement
            | SearchFunStatementList SearchFunStatement
            ;
     */
    private List<ASTree> searchFunStatementList() {
        final var list = new ArrayList<ASTree>();
        list.add(this.searchFunStatement());

        while (this.lookahead() != null && !this.test(ClosedCurlyBracketToken.class)) {
            list.add(this.searchFunStatement());
            this.ignoreNewLines();
        }
        return list;
    }

    /*
        SearchFunStatement
            : SimpleIfStatement
            | ArrowIfStatement
            | FunCallExpression
            | ZipSqlLiteral
            ;
     */
    private ASTree searchFunStatement() {
        this.ignoreNewLines();

        // ArrowIfStatement
        if (this.tokenMatching(ArrowToken.class, NewLineToken.class)) {
            return this.arrowIfStatement();
        }

        // SimpleIfStatement
        if (this.tokenMatching(QuestionMarkToken.class, NewLineToken.class)
                || this.tokenMatching(QuestionMarkPointToken.class, NewLineToken.class)
        ) {
            return this.simpleIfStatement();
        }

        // FunCallExpression
        if (this.test(ColonToken.class)) {
            return this.funCallExpression();
        }

        // ZipSqlLiteral
        return this.zipSqlLiteral();
    }

    /*
        ArrowIfStatement
            : ZipSqlLiteral MemberExpression '->' ConditionalExpression NewLines
            ;
    */
    private ArrowIfStatement arrowIfStatement() {
        final var zipSqlLiteral = this.zipSqlLiteral();
        if (zipSqlLiteral.value().isBlank()) {
            final var exStr = String.format(
                    "Unsupported syntax. Illegal dangling control statement -> Row: [%s]",
                    this.lookahead().tokenLocation().lineNumer()
            );
            throw new SyntaxException(exStr, this.lookahead());
        }

        if (!this.test(ColonToken.class)) {
            final var exStr = String.format(
                    "Unsupported syntax. The parameter information that was called does not exist. Correct way: [sql :username -> conditionalExpression] -> Row: [%s]",
                    this.lookahead().tokenLocation().lineNumer()
            );
            throw new SyntaxException(exStr, this.lookahead());
        }

        // MemberExpression
        final var memberExpression = this.memberExpression();

        // ->
        this.consume(ArrowToken.class);

        // ConditionalExpression
        final var test = this.conditionalExpression();

        this.ignoreNewLines(true);
        return new ArrowIfStatement(memberExpression, test, zipSqlLiteral);
    }

    /*
        ConditionalExpression
            : EqualityExpression
            ;
     */
    private ASTree conditionalExpression() {
        return this.equalityExpression();
    }

    /*
        EQUALITY_OPERATOR: ==, !=
        x == y
        x != y
        
        EqualityExpression
            : LeftHandSideExpression
            | EqualityExpression EQUALITY_OPERATOR LeftHandSideExpression
            ;
     */
    private ASTree equalityExpression() {
        ASTree left = this.leftHandSideExpression();

        while (this.test(EqualityToken.class)) {
            EqualityToken operator = this.consume(EqualityToken.class);

            ASTree right = this.leftHandSideExpression();
            left = new BinaryExpression(operator, left, right);
        }

        return left;
    }

    /*
        LeftHandSideExpression
            : PrimaryExpression
            | FunCallExpression
            | ThisFunCallExpression
            ;
     */
    private ASTree leftHandSideExpression() {
        if (this.test(ColonToken.class)) {
            return this.funCallExpression();
        }

        if (this.test(ThisToken.class)) {
            return this.thisFunCallExpression();
        }

        return this.primaryExpression();
    }

    /*
        thisFunCallExpression
            : ThisExpression
            | 'this' '::' Identifier OptArguments
            ;
     */
    private ASTree thisFunCallExpression() {
        this.consume(ThisToken.class);

        if (this.test(DoubleColonToken.class)) {
            this.consume(DoubleColonToken.class);

            final var funName = this.consume(IdentifierToken.class).value();
            final var arguments = this.optArguments();
            return new ThisFunCallExpression(funName, arguments);
        }

        return this.thisExpression();
    }

    /*
        ThisExpression
            : 'this'
            ;
     */
    private ThisExpression thisExpression() {
        return new ThisExpression();
    }

    /*
        SimpleIfStatement
            | ZipSqlLiteral CallExpression
            ;
     */
    private ASTree simpleIfStatement() {
        final var zipSqlLiteral = this.zipSqlLiteral();
        if (zipSqlLiteral.value().isBlank()) {
            final var exStr = String.format(
                    "Unsupported syntax. Illegal dangling control statement -> Row: [%s]",
                    this.lookahead().tokenLocation().lineNumer()
            );
            throw new SyntaxException(exStr, this.lookahead());
        }

        final var callExpression = this.callExpression();

        return new SimpleIfStatement(callExpression, zipSqlLiteral);
    }

    /*
      将多个符合sql的token组合成一整个sql
      以换行或冒号分割
      
      where username = :username
      where username = 'nameKeyword' \n
  
      ZipSqlLiteral
          : sql NewLines
          ;
   */
    private SqlLiteral zipSqlLiteral() {
        final var list = new ArrayList<Token<?>>();

        while (!(this.test(ColonToken.class) || this.test(NewLineToken.class))) {
            list.add(this.consume(Token.class));
        }

        final var value = list.stream()
                .map(Token::value)
                .map(Objects::toString)
                .collect(Collectors.joining(" "));

        this.ignoreNewLines();
        return new SqlLiteral(value + " ");
    }

    /*
        CallExpression
            : SimpleLogicalFunCallExpression
            | LogicalFunCallExpression
            ;
     */
    private ASTree callExpression() {
        if (this.tokenMatching(QuestionMarkToken.class, NewLineToken.class)) {
            return this.simpleLogicalFunCallExpression();
        }

        return this.logicalFunCallExpression();
    }

    /*
        SimpleLogicalFunCallExpression
            : FunCallExpression '?' NewLines
            ;
     */
    private ASTree simpleLogicalFunCallExpression() {
        final var funCallExpression = this.funCallExpression();

        if (!this.test(QuestionMarkToken.class)) {
            final var exStr = String.format(
                    "Unsupported syntax. Maybe there's a question mark at the end -> Row: [%s]",
                    this.lookahead().tokenLocation().lineNumer());
            throw new SyntaxException(exStr, this.lookahead());
        }

        this.consume(QuestionMarkToken.class);
        this.ignoreNewLines(true);

        return funCallExpression;
    }

    /*
        LogicalFunCallExpression
            : FunCallExpression
            | FunCallExpression '?.' Identifier OptArguments NewLines
            ;
     */
    private ASTree logicalFunCallExpression() {
        final var funCallExpression = this.funCallExpression();

        if (this.test(QuestionMarkPointToken.class)) {
            this.consume(QuestionMarkPointToken.class);
            final var funName = this.consume(IdentifierToken.class).value();
            final var arguments = this.optArguments();

            this.ignoreNewLines(true);

            return new LogicalFunCallExpression(funCallExpression, funName, arguments);
        }

        return funCallExpression;

    }

    /*
        FunCallExpression
            : MemberExpression
            | MemberExpression '::' Identifier OptArguments
            ;
     */
    private ASTree funCallExpression() {
        final var memberExpression = this.memberExpression();

        if (this.test(DoubleColonToken.class)) {
            this.consume(DoubleColonToken.class);

            final var funName = this.consume(IdentifierToken.class).value();
            final var arguments = this.optArguments();

            return new FunCallExpression(memberExpression, funName, arguments);
        }

        return memberExpression;
    }

    /*
        OptArguments
            : '(' OptArgumentList ')'
            ;
     */
    private List<ASTree> optArguments() {
        if (this.test(OpenParenthesisToken.class)) {
            this.consume(OpenParenthesisToken.class);

            // OptArgumentList
            final var list = this.test(ClosedParenthesisToken.class)
                    ? List.<ASTree>of()
                    : this.argumentList();


            this.consume(ClosedParenthesisToken.class);
            return list;
        }

        return List.of();
    }

    /*
        ArgumentList
            : Argument
            | ArgumentList ',' Argument
            ;
     */
    private List<ASTree> argumentList() {
        final var argumentList = new ArrayList<ASTree>();
        argumentList.add(this.argument());

        while (this.test(CommaToken.class)) {
            this.consume(CommaToken.class);
            argumentList.add(this.argument());
        }

        return argumentList;
    }

    /*
        Argument
            : PrimaryExpression
            ;
     */
    private ASTree argument() {
        return this.primaryExpression();
    }

    /*
        PrimaryExpression
            : MemberExpression
            | Literal
            ;
     */
    private ASTree primaryExpression() {
        // MemberExpression
        if (this.test(ColonToken.class)) {
            return this.memberExpression();
        }

        // Literal
        if (this.test(LiteralToken.class)) {
            return this.literal();
        }

        throw new SyntaxException("(primaryExpression) 未支持的类型: " + this.lookahead(), this.lookahead());
    }

    /*
        MemberExpression
            : ':' Members
            ;
     */
    private MemberExpression memberExpression() {
        this.consume(ColonToken.class);
        return this.members();
    }

    /*
        MemberExpression
            : Identifier
            | MemberExpression '.' Identifier
            ;
     */
    private MemberExpression members() {
        ASTree member = this.identifier();

        ASTree property = null;
        while (this.test(DotToken.class)) {
            this.consume(DotToken.class);
            property = this.identifier();
            member = new MemberExpression(member, property);
        }

        if (property != null) {
            return ((MemberExpression) member);
        }

        return new MemberExpression(member, null);
    }

    /*
        ImportStatementList
            : ImportStatement
            | ImportStatementList ImportStatement
            ;
     */
    private ImportStatementList importStatementList() {
        final var list = new ArrayList<ImportStatement>();

        list.add(this.importStatement());

        while (this.test(ImportToken.class)) {
            list.add(this.importStatement());
        }

        return new ImportStatementList(list);
    }

    /*
      ImportStatement
          : 'import' ImportPath NewLines
          ;
   */
    private ImportStatement importStatement() {
        this.consume(ImportToken.class);
        Pair<Identifier[], Boolean> importedPath = this.importPath();
        this.ignoreNewLines(true);

        return new ImportStatement(importedPath.first(), importedPath.second());
    }

    /*
        ImportPath
            : Identifier
            | ImportPath '.' Identifier
            | ImportPath '.' '*'
            ;
     */
    private Pair<Identifier[], Boolean> importPath() {
        final var list = new ArrayList<Identifier>();
        list.add(this.identifier());

        boolean endAsterisk = false;
        while (this.test(DotToken.class)) {
            this.consume(DotToken.class);

            if (this.test(MultiplicativeToken.MULTIPLICATION.getClass())) {
                endAsterisk = true;
                this.consume(MultiplicativeToken.MULTIPLICATION.getClass());
                // 如果当前符号为星号, 则解析结束
                break;
            } else {
                list.add(this.identifier());
            }
        }

        return new Pair<>(list.toArray(new Identifier[]{}), endAsterisk);
    }

    /*
        EntityStatement
            : entity NamespacePath NewLines
            ;
     */
    private EntityStatement entityStatement() {
        this.consume(EntityToken.class);

        final var identifiers = this.namespacePath();
        this.ignoreNewLines(true);
        return new EntityStatement(identifiers);
    }

    /*
      NamespaceStatement
          : 'namespace' NamespacePath NewLines
          ;
   */
    private NamespaceStatement namespaceStatement() {
        this.consume(NamespaceToken.class);

        final var identifiers = this.namespacePath();
        this.ignoreNewLines(true);
        return new NamespaceStatement(identifiers);
    }

    /*
        NamespacePath
     */
    private List<Identifier> namespacePath() {
        final var list = new ArrayList<Identifier>();
        list.add(this.identifier());
        while (this.test(DotToken.class)) {
            this.consume(DotToken.class);

            if (this.test(MultiplicativeToken.MULTIPLICATION.getClass())) {
                throw new SyntaxException("Asterisks are not allowed in namespaces.", this.lookahead());
            }

            list.add(this.identifier());
        }

        return list;
    }

    private Identifier identifier() {
        final var identifierToken = this.consume(IdentifierToken.class);
        return new Identifier(identifierToken.value());
    }

    /*
        Literal
            : BooleanLiteral
            | NullLiteral
            | StringLiteral
            | NumericLiteral
            ;
     */
    private ASTree literal() {
        if (this.test(BooleanToken.class)) {
            return this.booleanLiteral();
        }

        if (this.test(NullToken.class)) {
            return this.nullLiteral();
        }

        if (this.test(StringLiteralToken.class)) {
            return this.stringLiteral();
        }

        if (this.test(NumericLiteralToken.class)) {
            return this.numericLiteral();
        }

        throw new SyntaxException("Literal: unexpected literal production\n" + this.lookahead(), this.lookahead());
    }

    /*
        BooleanLiteral
            : 'true'
            | 'false'
            ;
     */
    private BooleanLiteral booleanLiteral() {
        final var token = this.consume(BooleanToken.class);
        return new BooleanLiteral(token.value());
    }

    /*
        StringLiteral
            : STRING
            ;
     */
    private StringLiteral stringLiteral() {
        final var token = this.consume(StringLiteralToken.class);
        final var v = token.value().substring(1, token.value().length() - 1);
        return new StringLiteral(v, token.value());
    }

    /*
        NullLiteral
            : 'null'
            ;
     */
    private NullLiteral nullLiteral() {
        this.consume(NullToken.class);

        return new NullLiteral();
    }

    /*
        NumericLiteral
            : NUMBER
            ;
     */
    private NumericLiteral numericLiteral() {
        final var token = this.consume(NumericLiteralToken.class);
        return new NumericLiteral(token.value());
    }

    /**
     * 消耗所有换行, 直到不是换行为止
     * 不强制消耗, 存在则消耗
     */
    private void ignoreNewLines() {
        this.ignoreNewLines(false);
    }

    /**
     * 消耗所有换行, 直到不是换行为止
     *
     * @param forceOne 强制消耗一个, 如果不存在换行将报错
     */
    private void ignoreNewLines(boolean forceOne) {
        if (forceOne) {
            this.consume(NewLineToken.class);
        }

        while (this.test(NewLineToken.class)) {
            this.consume(NewLineToken.class);
        }
    }

}
