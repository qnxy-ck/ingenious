package org.nds.token.keyword;

import org.nds.token.StringValueToken;
import org.nds.token.TokenLocation;

/**
 * @author Qnxy
 * 2023/10/13
 */
public record NullToken(
        String value,
        TokenLocation tokenLocation
) implements StringValueToken {
}
