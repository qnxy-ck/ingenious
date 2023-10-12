package org.nds.token;

/**
 * @author Qnxy
 * 2023/10/12
 */
public record IdentifierToken(
        String value,
        TokenLocation tokenLocation
) implements StringValueToken {
}