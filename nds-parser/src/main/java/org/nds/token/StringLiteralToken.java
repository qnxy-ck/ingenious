package org.nds.token;

/**
 * @author Qnxy
 * 2023/10/13
 */
public record StringLiteralToken(
        String value,
        TokenLocation tokenLocation
) implements LiteralToken<String> {
}
