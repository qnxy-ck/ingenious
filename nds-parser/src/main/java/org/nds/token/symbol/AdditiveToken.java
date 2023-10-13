package org.nds.token.symbol;

import org.nds.token.OperatorToken;
import org.nds.token.TokenLocation;

import java.util.Arrays;

/**
 * 加减法运算符
 *
 * @author Qnxy
 * 2023/10/12
 */
public enum AdditiveToken implements OperatorToken {
    ADDITION("+"),
    SUBTRACTION("-");

    private final String operator;
    private TokenLocation tokenLocation;

    AdditiveToken(String operator) {
        this.operator = operator;
    }

    public static AdditiveToken operatorOf(String operator, TokenLocation tokenLocation) {
        final var additiveToken = Arrays.stream(values())
                .filter(it -> it.operator.equals(operator))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unmatched symbol: " + operator));

        additiveToken.setTokenLocation(tokenLocation);
        return additiveToken;
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
