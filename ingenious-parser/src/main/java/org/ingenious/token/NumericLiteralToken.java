package org.ingenious.token;

/**
 * @author Qnxy
 */
public record NumericLiteralToken(
        String value,
        TokenLocation tokenLocation
) implements LiteralToken<String> {
}