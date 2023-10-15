package org.ingenious.token;

/**
 * @author Qnxy
 */
public record IdentifierToken(
        String value,
        TokenLocation tokenLocation
) implements StringValueToken {
}