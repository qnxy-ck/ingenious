package org.nds.token.keyword;

import org.nds.token.LiteralToken;
import org.nds.token.TokenLocation;

/**
 * @author Qnxy
 * 2023/10/13
 */
public record NullToken(
        String value,
        TokenLocation tokenLocation
) implements LiteralToken<String> {
}
