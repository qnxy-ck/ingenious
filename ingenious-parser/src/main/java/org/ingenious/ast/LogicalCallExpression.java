package org.ingenious.ast;

/**
 * @author Qnxy
 */
public record LogicalCallExpression(
        // 调用者
        ASTree caller,

        // 被调用的函数
        ASTree callFunc
) implements ASTree {
}
