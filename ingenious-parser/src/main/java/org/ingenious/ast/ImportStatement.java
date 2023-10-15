package org.ingenious.ast;

/**
 * 导入声明
 *
 * @author Qnxy
 */
public record ImportStatement(
        String value,

        // 是一个实体类的导入
        boolean entity
) implements ASTree {
}
