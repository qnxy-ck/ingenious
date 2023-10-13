package org.nds.ast;

/**
 * @author Qnxy
 * 2023/10/13
 */
public record ArrowIfStatement(
        ASTree member,
        ASTree test,
        ASTree consequent
) implements ASTree {
}
