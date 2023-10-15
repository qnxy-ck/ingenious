package org.ingenious.ast;

/**
 * @author Qnxy
 */
public record SimpleIfStatement(
        // 条件
        ASTree test,

        // 控制内容
        ASTree consequent
) implements ASTree {
}
