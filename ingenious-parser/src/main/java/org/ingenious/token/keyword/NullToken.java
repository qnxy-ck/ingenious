package org.ingenious.token.keyword;

import org.ingenious.token.LiteralToken;
import org.ingenious.token.TokenLocation;

/**
 * @author Qnxy
 */
public record NullToken(
        String value,
        TokenLocation tokenLocation
) implements LiteralToken<String> {
}
