package org.ingenious.ast;

/**
 * 命名空间声明
 *
 * @author Qnxy
 */
public record NamespaceStatement(
        String value
) implements ASTree {
}
