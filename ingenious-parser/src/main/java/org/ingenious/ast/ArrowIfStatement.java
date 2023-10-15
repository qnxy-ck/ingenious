package org.ingenious.ast;

/**
 * @author Qnxy
 */
public record ArrowIfStatement(
        ASTree member,
        ASTree test,
        ASTree consequent
) implements ASTree {
}
