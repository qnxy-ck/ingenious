package org.ingenious.token.symbol;

import org.ingenious.token.OperatorToken;
import org.ingenious.token.TokenLocation;

/**
 * =
 *
 * @author Qnxy
 */
public record SimpleAssignToken(
        String value,
        TokenLocation tokenLocation
) implements OperatorToken {
}
