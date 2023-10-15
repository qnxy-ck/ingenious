package org.ingenious.ast;

/**
 * @author Qnxy
 */
public record SqlLiteral(
        String value
) implements ASTree {
}
