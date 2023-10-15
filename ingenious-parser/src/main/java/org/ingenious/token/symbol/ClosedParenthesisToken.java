package org.ingenious.token.symbol;

import org.ingenious.token.StringValueToken;
import org.ingenious.token.TokenLocation;

/**
 * )
 *
 * @author Qnxy
 */
public record ClosedParenthesisToken(
        String value,
        TokenLocation tokenLocation
) implements StringValueToken {
}
