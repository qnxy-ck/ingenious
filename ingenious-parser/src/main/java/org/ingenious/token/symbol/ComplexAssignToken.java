package org.ingenious.token.symbol;

import org.ingenious.token.OperatorToken;
import org.ingenious.token.TokenLocation;

import java.util.Arrays;

/**
 * 复杂赋值运算符 *=, /=, +=. -=
 *
 * @author Qnxy
 */
public enum ComplexAssignToken implements OperatorToken {
    MUL_ASSIGN("*="),
    DIV_ASSIGN("/="),
    ADD_ASSIGN("+="),
    SUB_ASSIGN("-=");

    private final String operator;
    private TokenLocation tokenLocation;

    ComplexAssignToken(String operator) {
        this.operator = operator;
    }

    public static ComplexAssignToken operatorOf(String operator, TokenLocation tokenLocation) {
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
