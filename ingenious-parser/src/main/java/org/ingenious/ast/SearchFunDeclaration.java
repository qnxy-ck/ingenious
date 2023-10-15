package org.ingenious.ast;

import java.util.List;

/**
 * 查询方法的定义信息
 *
 * @author Qnxy
 */
public record SearchFunDeclaration(
        // 方法名称
        String funName,

        // 参数列表
        List<FormalParameter> formalParameters,

        // 返回类型
        FunTypeDeclaration returnType,

        // 方法体
        SearchFunBlockStatement blockStatement
) implements ASTree {
}