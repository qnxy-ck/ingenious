package org.nds.ast;

/**
 * @author Qnxy
 * 2023/10/13
 */
public record BooleanLiteral(
        boolean value
) implements ASTree {
}