package org.nds.token.symbol;

import org.nds.token.StringValueToken;
import org.nds.token.TokenLocation;

import java.util.Arrays;

/**
 * 乘除法运算
 *
 * @author Qnxy
 * 2023/10/12
 */
public enum MultiplicativeToken implements StringValueToken {
    MULTIPLICATION("*"),
    DIVISION("/");

    private final String operator;
    private TokenLocation tokenLocation;

    MultiplicativeToken(String operator) {
        this.operator = operator;
    }

    public static MultiplicativeToken operatorOf(String operator, TokenLocation tokenLocation) {
        final var multiplicativeToken = Arrays.stream(values())
                .filter(it -> it.operator.equals(operator))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unmatched symbol: " + operator));

        multiplicativeToken.setTokenLocation(tokenLocation);
        return multiplicativeToken;
    }

    @Override
    public String value() {
        return this.operator;
    }

    @Override
    public TokenLocation tokenLocation() {
        return this.tokenLocation;
    }

    public void setTokenLocation(TokenLocation tokenLocation) {
        this.tokenLocation = tokenLocation;
    }
}
