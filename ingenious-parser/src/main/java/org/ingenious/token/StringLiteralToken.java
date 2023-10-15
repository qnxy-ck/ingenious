package org.ingenious.token;

/**
 * @author Qnxy
 */
public record StringLiteralToken(
        String value,
        TokenLocation tokenLocation
) implements LiteralToken<String> {
}
