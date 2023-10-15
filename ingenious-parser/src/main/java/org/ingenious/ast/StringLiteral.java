package org.ingenious.ast;

/**
 * @author Qnxy
 */
public record StringLiteral(
        String value,
        String rawValue
) implements ASTree {

}
