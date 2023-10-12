package org.nds.ast;

/**
 * 形参信息
 *
 * @author Qnxy
 * 2023/10/13
 */
public record FormalParameter(
        // 参数名称
        String parameterName,

        // 类型定义
        FunTypeDeclaration parameterType
) implements ASTree {
}
