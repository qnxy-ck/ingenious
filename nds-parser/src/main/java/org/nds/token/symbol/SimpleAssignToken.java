package org.nds.token.symbol;

import org.nds.token.OperatorToken;
import org.nds.token.TokenLocation;

/**
 * =
 *
 * @author Qnxy
 * 2023/10/13
 */
public record SimpleAssignToken(
        String value,
        TokenLocation tokenLocation
) implements OperatorToken {
}
