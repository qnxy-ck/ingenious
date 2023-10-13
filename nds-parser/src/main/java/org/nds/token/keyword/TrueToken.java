package org.nds.token.keyword;

import org.nds.token.BooleanToken;
import org.nds.token.TokenLocation;

/**
 * @author Qnxy
 * 2023/10/13
 */
public record TrueToken(
        Boolean value,
        TokenLocation tokenLocation
) implements BooleanToken {
}
