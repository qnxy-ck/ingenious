package org.ingenious.lexer;

import org.ingenious.token.Token;
import org.ingenious.token.TokenLocation;

import java.util.function.BiFunction;
import java.util.regex.Pattern;

/**
 * @author Qnxy
 */
public record MatchingInformation<V>(
        Pattern pattern,
        BiFunction<String, TokenLocation, Token<V>> tokenFun
) {
}
