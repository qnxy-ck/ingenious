package org.nds.token;

/**
 * 换行符
 *
 * @author Qnxy
 * 2023/10/12
 */
public record NewLineToken(
        String value,
        TokenLocation tokenLocation
) implements StringValueToken {

}
