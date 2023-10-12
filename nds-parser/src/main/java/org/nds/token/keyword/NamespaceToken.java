package org.nds.token.keyword;

import org.nds.token.StringValueToken;
import org.nds.token.TokenLocation;

/**
 * @author Qnxy
 * 2023/10/12
 */
public record NamespaceToken(
        String value,
        TokenLocation tokenLocation
) implements StringValueToken {
}
