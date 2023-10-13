package org.nds.parser;

import org.nds.ast.*;
import org.nds.lexer.Lexer;
import org.nds.token.*;
import org.nds.token.keyword.*;
import org.nds.token.symbol.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 语法解析器-构建AST
 *
 * @author Qnxy
 * 2023/10/12
 */
public final class Parser {

    private final Lexer lexer;
    private final Queue<Token<?>> tokens = new ArrayDeque<>();

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.tokens.offer(this.lexer.getNextToken());
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
        list.add(this.importEntityStatement());

        // OptImportStatementList
        if (this.test(ImportToken.class)) {
            list.addAll(this.importStatementList());
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
        final var multiple = this.test(MulToken.class);

        // OptMul
        if (multiple) {
            this.consume(MulToken.class);
        }

        final var typeValue = this._typeValue();
        this.ignoreNewLines();
        return new FunTypeDeclaration(multiple, typeValue);
    }

    /*
        SearchReturnDeclaration
            : ':' TypeValue
            | ':' 'mul' TypeValue
            ;
     */
    private FunTypeDeclaration searchReturnDeclaration() {
        this.consume(ColonToken.class);
        final var multiple = this.test(MulToken.class);

        if (multiple) {
            this.consume(MulToken.class);
        }

        if (this.test(OpenCurlyBracketToken.class)) {
            return FunTypeDeclaration.MUL_DEFAULT;
        }

        final var typeValue = this._typeValue();
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
            : ZipSqlLiteral
            | SimpleIfStatement
            ;
     */
    private ASTree searchFunStatement() {
        this.ignoreNewLines();

        if (this.tokenMatching(QuestionMarkToken.class, NewLineToken.class)
                || this.tokenMatching(QuestionMarkPointToken.class, NewLineToken.class)
        ) {
            return this.simpleIfStatement();
        }

        if (this.test(ColonToken.class)) {
            return this.funCallExpression();
        }

        return this.zipSqlLiteral();
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
                    "Unsupported syntax. Illegal dangling control statement -> [%s]",
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

        while (!(
                this.test(ColonToken.class)
                        || this.test(NewLineToken.class)
        )) {
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
                    "Unsupported syntax. Maybe there's a question mark at the end -> [%s]",
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
            : Members
            | BooleanLiteral
            | StringLiteral
            | NullLiteral
            | NumericLiteral
            ;
     */
    private ASTree argument() {
        if (this.test(ColonToken.class)) {
            return this.memberExpression();
        }

        if (this.test(BooleanToken.class)) {
            return this.booleanLiteral();
        }

        if (this.test(StringLiteralToken.class)) {
            return this.stringLiteral();
        }

        if (this.test(NullToken.class)) {
            return this.nullLiteral();
        }

        if (this.test(NumericLiteralToken.class)) {
            return this.numericLiteral();
        }

        throw new SyntaxException("(argument) 未支持的类型: " + this.lookahead(), this.lookahead());
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
        NamespaceStatement
            : 'namespace' NamespaceDefinition NewLines
            ;
     */
    private NamespaceStatement namespaceStatement() {
        this.consume(NamespaceToken.class);

        final String namespace = this._namespaceDefinition(false);
        this.ignoreNewLines(true);
        return new NamespaceStatement(namespace);
    }

    /*
        ImportEntityStatement
            : 'import' 'entity' NamespaceDefinition NewLines
            ;
     */
    private ImportStatement importEntityStatement() {
        this.consume(ImportToken.class);
        this.consume(EntityToken.class);

        final String entityValue = this._namespaceDefinition(false);
        this.ignoreNewLines(true);
        return new ImportStatement(entityValue, true);
    }

    /*
        ImportStatementList
            : ImportStatement
            | ImportStatementList ImportStatement
            ;
     */
    private List<ImportStatement> importStatementList() {
        final var list = new ArrayList<ImportStatement>();

        list.add(this.importStatement());

        while (this.test(ImportToken.class)) {
            list.add(this.importStatement());
        }

        return list;
    }

    /*
      ImportStatement
          : 'import' NamespaceDefinition NewLines
          ;
   */
    private ImportStatement importStatement() {
        this.consume(ImportToken.class);

        final String entityValue = this._namespaceDefinition(true);
        this.ignoreNewLines(true);
        return new ImportStatement(entityValue, false);
    }

    private Identifier identifier() {
        final var identifierToken = this.consume(IdentifierToken.class);
        return new Identifier(identifierToken.value());
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

    /*
        TypeValue
     */
    private String _typeValue() {
        return this._namespaceDefinition(false);
    }

    private String _namespaceDefinition(boolean asterisk) {
        var sb = new StringBuilder();

        sb.append(this.consume(IdentifierToken.class).value());

        while (this.test(DotToken.class)) {
            sb.append(this.consume(DotToken.class).value());

            if (asterisk && this.test(MultiplicativeToken.MULTIPLICATION.getClass())) {
                sb.append(this.consume(MultiplicativeToken.MULTIPLICATION.getClass()).value());
                break;
            }

            sb.append(this.consume(IdentifierToken.class).value());
        }

        return sb.toString();
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

    // --------------------------------------------------------
    // 辅助方法

    private Token<?> lookahead() {
        return this.tokens.peek();
    }

    /**
     * 匹配lookahead
     *
     * @param tokenType 待匹配的token类型
     * @return true 匹配成功
     */
    private boolean test(Class<? extends Token<?>> tokenType) {
        return tokenType.isInstance(this.lookahead());
    }

    /**
     * 匹配token直到找到为止, 如果匹配过程中遇到了stopTokenType, 则会提前结束 返回false未找到
     *
     * @param tokenType     待匹配的token类型
     * @param stopTokenType 停止匹配的token类型
     * @return true 匹配成功
     */
    private boolean tokenMatching(Class<? extends Token<?>> tokenType, Class<? extends Token<?>> stopTokenType) {
        // 优先匹配队列中的token
        for (Token<?> t : this.tokens) {
            if (tokenType.isInstance(t)) {
                return true;
            }

            if (stopTokenType.isInstance(t)) {
                return false;
            }
        }

        var token = this.lookahead();

        while (true) {
            if (tokenType.isInstance(token)) {
                return true;
            }

            // 如果没有找到则继续向下获取
            // 获取到的token添加到队列中
            token = this.lexer.getNextToken();
            if (token == null) {
                return false;
            }

            this.tokens.offer(token);
            if (stopTokenType.isInstance(token)) {
                return false;
            }
        }
    }

    /**
     * 消费一个token并更新lookahead
     *
     * @param tokenType 消费token的类型
     * @param <V>       token的类型
     * @return 被消费的token信息
     */
    private <V extends Token<?>> V consume(Class<V> tokenType) {
        final var token = this.tokens.poll();

        if (token == null) {
            throw new SyntaxException("Unexpected end of input, expected: " + tokenType.getSimpleName(), null);
        }

        if (!tokenType.isInstance(token)) {
            final var exStr = String.format(
                    "Unexpected token: %s, expected: %s",
                    token,
                    tokenType.getSimpleName()
            );
            throw new SyntaxException(exStr, token);
        }

        // 继续获取下一个
        final var nextToken = this.lexer.getNextToken();
        if (nextToken != null) {
            this.tokens.offer(nextToken);
        }

        //noinspection unchecked
        return (V) token;
    }


}
