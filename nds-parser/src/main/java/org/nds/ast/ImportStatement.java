package org.nds.ast;

/**
 * 导入声明
 *
 * @author Qnxy
 * 2023/10/13
 */
public record ImportStatement(
        String value,

        // 是一个实体类的导入
        boolean entity
) implements ASTree {
}
