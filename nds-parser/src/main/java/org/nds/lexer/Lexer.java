package org.nds.lexer;

import org.nds.token.*;
import org.nds.token.keyword.*;
import org.nds.token.symbol.*;

import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * 词法分析
 *
 * @author Qnxy
 * 2023/10/12
 */
public final class Lexer {

    private static final MatchingInformation<?>[] MATCHING_INFORMATION_ARR = new MatchingInformation[]{
            // -----------------------------------------------------------------------------------------
            // 匹配换行
            new MatchingInformation<>(compile("^\\n"), (v, it) -> new NewLineToken(null, it)),

            // -----------------------------------------------------------------------------------------
            // 跳过空白符
            new MatchingInformation<>(compile("^[ \t]+"), Lexer::nullBiFun),

            // -----------------------------------------------------------------------------------------
            // 跳过单行注释
            new MatchingInformation<>(compile("^//.*"), Lexer::nullBiFun),

            // -----------------------------------------------------------------------------------------
            // 跳过多行注释
            new MatchingInformation<>(compile("^/\\*[\\s\\S]*?\\*/"), Lexer::nullBiFun),

            // -----------------------------------------------------------------------------------------
            // 符号
            new MatchingInformation<>(compile("^\\("), OpenParenthesisToken::new),
            new MatchingInformation<>(compile("^\\)"), ClosedParenthesisToken::new),
            new MatchingInformation<>(compile("^\\{"), OpenCurlyBracketToken::new),
            new MatchingInformation<>(compile("^}"), ClosedCurlyBracketToken::new),
            new MatchingInformation<>(compile("^\\."), DotToken::new),
            new MatchingInformation<>(compile("^,"), CommaToken::new),
            new MatchingInformation<>(compile("^::"), DoubleColonToken::new),
            new MatchingInformation<>(compile("^:"), ColonToken::new),
            new MatchingInformation<>(compile("^\\?\\."), QuestionMarkPointToken::new),
            new MatchingInformation<>(compile("^\\?"), QuestionMarkToken::new),
            new MatchingInformation<>(compile("^->"), ArrowToken::new),
            new MatchingInformation<>(compile("^\\$"), DollarToken::new),


            // -----------------------------------------------------------------------------------------
            // 数字            
            new MatchingInformation<>(compile("^\\d+"), NumericLiteralToken::new),

            // -----------------------------------------------------------------------------------------
            // 等式运算符: ==, !=
            new MatchingInformation<>(compile("^[=!]="), EqualityToken::operatorOf),

            // -----------------------------------------------------------------------------------------
            // 赋值运算符: =, *=, /=, +=. -=
            new MatchingInformation<>(compile("^="), SimpleAssignToken::new),
            new MatchingInformation<>(compile("^[*/+-]="), ComplexAssignToken::operatorOf),


            // -----------------------------------------------------------------------------------------
            // 数学运算符: +, -, *, /
            new MatchingInformation<>(compile("^[+-]"), AdditiveToken::operatorOf),
            new MatchingInformation<>(compile("^[*/]"), MultiplicativeToken::operatorOf),


            // -----------------------------------------------------------------------------------------
            // 关键字
            new MatchingInformation<>(compile("^\\bnamespace\\b"), NamespaceToken::new),
            new MatchingInformation<>(compile("^\\bimport\\b"), ImportToken::new),
            new MatchingInformation<>(compile("^\\bentity\\b"), EntityToken::new),
            new MatchingInformation<>(compile("^\\bsearch\\b"), SearchToken::new),
            new MatchingInformation<>(compile("^\\bmul\\b"), MulToken::new),


            new MatchingInformation<>(compile("^\\btrue\\b"), Lexer::boolToken),
            new MatchingInformation<>(compile("^\\bfalse\\b"), Lexer::boolToken),
            new MatchingInformation<>(compile("^\\bnull\\b"), NullToken::new),


            // -----------------------------------------------------------------------------------------
            // 标识符
            new MatchingInformation<>(compile("^\\w+"), IdentifierToken::new),


            // -----------------------------------------------------------------------------------------
            // 字符串
            new MatchingInformation<>(compile("^\"[^\"]*\""), StringLiteralToken::new),
            new MatchingInformation<>(compile("^'[^']*'"), StringLiteralToken::new),

    };
    private final String text;

    // --------------------------------------------------------------------------
    private int line = 1;
    private int column = 1;
    private int cursor = 0;

    public Lexer(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("The text to be parsed cannot be null.");
        }
        this.text = text;
    }

    private static <V> Token<V> nullBiFun(String v, TokenLocation tokenLocation) {
        return null;
    }

    private static Token<Boolean> boolToken(String v, TokenLocation tokenLocation) {
        final var f = Boolean.parseBoolean(v);
        return f ? new TrueToken(true, tokenLocation) : new FalseToken(false, tokenLocation);
    }

    /**
     * 获取下一个token
     * 无法匹配到正确token时发生词法错误异常
     *
     * @param <V> token内容类型
     * @return token
     */
    public <V> Token<V> getNextToken() {
        if (!this.hasMoreTokens()) {
            return null;
        }

        final String t = this.text.substring(this.cursor);

        for (MatchingInformation<?> matching : MATCHING_INFORMATION_ARR) {
            String tokenValue = this.match(matching.pattern(), t);

            if (tokenValue == null) {
                continue;
            }

            this.cursor += tokenValue.length();

            final Token<?> token = this.makeToken(tokenValue, matching.tokenFun());
            this.column += tokenValue.length();

            if (token == null) {
                return this.getNextToken();
            }

            if (token instanceof NewLineToken) {
                this.line += 1;
                this.column = 1;
            }

            //noinspection unchecked
            return (Token<V>) token;

        }

        throw new LexerException("Unmatched symbol", this.line, this.column);
    }

    private boolean hasMoreTokens() {
        return this.cursor < this.text.length();
    }

    private String match(Pattern pattern, String str) {
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

    /**
     * 构建token
     */
    private <V> Token<V> makeToken(String tokenValue, BiFunction<String, TokenLocation, Token<V>> tokenFun) {

        if (tokenFun == null) {
            return null;
        }

        return tokenFun.apply(tokenValue, new TokenLocation(this.line, this.column));
    }


}
