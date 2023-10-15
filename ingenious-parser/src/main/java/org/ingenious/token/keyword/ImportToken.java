package org.ingenious.token.keyword;

import org.ingenious.token.StringValueToken;
import org.ingenious.token.TokenLocation;

/**
 * @author Qnxy
 */
public record ImportToken(
        String value,
        TokenLocation tokenLocation
) implements StringValueToken {
}
