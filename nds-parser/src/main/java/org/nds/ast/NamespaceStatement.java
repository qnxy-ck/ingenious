package org.nds.ast;

/**
 * 命名空间声明
 *
 * @author Qnxy
 * 2023/10/12
 */
public record NamespaceStatement(
        String value
) implements ASTree {
}
