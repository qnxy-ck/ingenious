package org.nds.lexer;

import org.nds.token.Token;
import org.nds.token.TokenLocation;

import java.util.function.BiFunction;
import java.util.regex.Pattern;

/**
 * @author Qnxy
 * 2023/10/12
 */
public record MatchingInformation<V>(
        Pattern pattern,
        BiFunction<String, TokenLocation, Token<V>> tokenFun
) {
}
