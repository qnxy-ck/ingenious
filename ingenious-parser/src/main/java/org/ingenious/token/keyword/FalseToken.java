package org.ingenious.token.keyword;

import org.ingenious.token.BooleanToken;
import org.ingenious.token.TokenLocation;

/**
 * @author Qnxy
 */
public record FalseToken(
        Boolean value,
        TokenLocation tokenLocation
) implements BooleanToken {
}
