package org.ingenious.ast;

/**
 * sql :xxx?.test
 *
 * @author Qnxy
 */
public record SimpleIfStatement(

        // 控制内容
        ASTree consequent,

        // 条件
        ASTree test
) implements ASTree {
}
