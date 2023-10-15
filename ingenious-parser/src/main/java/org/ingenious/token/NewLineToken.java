package org.ingenious.token;

/**
 * 换行符
 *
 * @author Qnxy
 */
public record NewLineToken(
        String value,
        TokenLocation tokenLocation
) implements StringValueToken {

}
