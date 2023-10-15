package org.ingenious.ast;

/**
 * 形参信息
 *
 * @author Qnxy
 */
public record FormalParameter(
        // 参数名称
        String parameterName,

        // 类型定义
        FunTypeDeclaration parameterType
) implements ASTree {
}
