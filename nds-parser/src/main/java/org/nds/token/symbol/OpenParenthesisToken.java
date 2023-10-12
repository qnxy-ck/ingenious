package org.nds.token.symbol;

import org.nds.token.StringValueToken;
import org.nds.token.TokenLocation;

/**
 * (
 *
 * @author Qnxy
 * 2023/10/12
 */
public record OpenParenthesisToken(
        String value,
        TokenLocation tokenLocation
) implements StringValueToken {
}
