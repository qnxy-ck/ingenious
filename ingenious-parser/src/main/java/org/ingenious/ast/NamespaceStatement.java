package org.ingenious.ast;

import java.util.List;

/**
 * 命名空间声明
 *
 * @author Qnxy
 */
public record NamespaceStatement(
        List<Identifier> values
) implements ASTree {
}
