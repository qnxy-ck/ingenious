package org.ingenious.ast;

import java.util.List;

/**
 * 数据类型定义
 *
 * @author Qnxy
 */
public record FunTypeDeclaration(
        // 数据类型是否为多个
        boolean multiple,

        // 具体类型
        List<Identifier> values
) implements ASTree {

    public static final FunTypeDeclaration DEFAULT = new FunTypeDeclaration(false, null);
    public static final FunTypeDeclaration MUL_DEFAULT = new FunTypeDeclaration(true, null);

}
