package org.ingenious.token.symbol;

import org.ingenious.token.OperatorToken;
import org.ingenious.token.TokenLocation;

import java.util.Arrays;

/**
 * 等式运算符 ==, !=
 *
 * @author Qnxy
 */
public enum EqualityToken implements OperatorToken {
    EQUALITY("=="),
    NOT_EQUALITY("!=");

    private final String operator;
    private TokenLocation tokenLocation;

    EqualityToken(String operator) {
        this.operator = operator;
    }

    public static EqualityToken operatorOf(String operator, TokenLocation tokenLocation) {
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

    @Override
    public String toString() {
        return "EqualityToken{" +
                "operator='" + operator + '\'' +
                ", tokenLocation=" + tokenLocation +
                '}';
    }
}