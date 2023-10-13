package org.nds.ast;

/**
 * @author Qnxy
 * 2023/10/13
 */
public record SqlLiteral(
        String value
) implements ASTree {
}
