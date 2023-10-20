package org.ingenious.ast;

/**
 * @author Qnxy
 */
public record ArrowIfStatement(

        // this 指向, 可为空 为空时 test 中不存在this
        MemberExpression member,
        ASTree test,
        ASTree consequent
) implements ASTree {
}
