package org.nds.ast;

/**
 * :username
 * :userInfo.username
 *
 * @author Qnxy
 * 2023/10/13
 */
public record MemberExpression(
        ASTree object,
        ASTree property
) implements ASTree {
}
