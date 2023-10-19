package org.ingenious.ast;

/**
 * 导入声明
 *
 * @author Qnxy
 */
public record ImportStatement(
        Identifier[] values,

        // 结尾是否存在星号, value中始终不存在星号, 使用此标志判断
        // true 存在
        boolean endAsterisk
) implements ASTree {
}
