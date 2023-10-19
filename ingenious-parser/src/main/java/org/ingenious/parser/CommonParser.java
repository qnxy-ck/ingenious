package org.ingenious.parser;

import org.ingenious.lexer.Lexer;
import org.ingenious.token.Token;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * 语法解析公共方法
 *
 * @author Qnxy
 */
public class CommonParser {

    private final Lexer lexer;
    private final Queue<Token<?>> tokens = new ArrayDeque<>();

    protected CommonParser(Lexer lexer) {
        this.lexer = lexer;
        this.tokens.offer(this.lexer.getNextToken());
    }

    protected Token<?> lookahead() {
        return this.tokens.peek();
    }

    /**
     * 匹配lookahead
     *
     * @param tokenType 待匹配的token类型
     * @return true 匹配成功
     */
    protected <T extends Token<?>> boolean test(Class<T> tokenType) {
        return tokenType.isInstance(this.lookahead());
    }

    /**
     * 匹配token直到找到为止, 如果匹配过程中遇到了stopTokenType, 则会提前结束 返回false未找到
     *
     * @param tokenType     待匹配的token类型
     * @param stopTokenType 停止匹配的token类型
     * @return true 匹配成功
     */
    protected boolean tokenMatching(Class<? extends Token<?>> tokenType, Class<? extends Token<?>> stopTokenType) {
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
    protected <V extends Token<?>> V consume(Class<V> tokenType) {
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
