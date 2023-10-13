package org.nds.ast;

/**
 * @author Qnxy
 * 2023/10/13
 */
public record SimpleIfStatement(
        // 条件
        ASTree test,

        // 控制内容
        ASTree consequent
) implements ASTree {
}
