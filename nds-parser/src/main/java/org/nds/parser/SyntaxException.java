package org.nds.parser;

import org.nds.token.Token;

/**
 * 语法错误
 *
 * @author Qnxy
 * 2023/10/12
 */
public class SyntaxException extends RuntimeException {
    // 在哪个token发生了错误
    private final Token<?> stopToken;

    public SyntaxException(String message, Token<?> stopToken) {
        super(message);
        this.stopToken = stopToken;
    }

    public Token<?> getStopToken() {
        return stopToken;
    }
}
