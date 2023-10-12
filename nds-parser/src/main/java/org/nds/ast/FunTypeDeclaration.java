package org.nds.ast;

/**
 * 数据类型定义
 *
 * @author Qnxy
 * 2023/10/13
 */
public record FunTypeDeclaration(
        // 数据类型是否为多个
        boolean multiple,

        // 具体类型
        String typeValue
) implements ASTree {

    public static final FunTypeDeclaration DEFAULT = new FunTypeDeclaration(false, null);
    public static final FunTypeDeclaration MUL_DEFAULT = new FunTypeDeclaration(true, null);

}
