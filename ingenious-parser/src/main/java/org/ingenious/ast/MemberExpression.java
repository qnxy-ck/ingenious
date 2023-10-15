package org.ingenious.ast;

/**
 * :username
 * :userInfo.username
 *
 * @author Qnxy
 */
public record MemberExpression(
        ASTree object,
        ASTree property
) implements ASTree {
}
