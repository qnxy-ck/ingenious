package org.ingenious.token.keyword;

import org.ingenious.token.StringValueToken;
import org.ingenious.token.TokenLocation;

/**
 * this
 *
 * @author Qnxy
 */
public record ThisToken(
        String value,
        TokenLocation tokenLocation
) implements StringValueToken {
}
