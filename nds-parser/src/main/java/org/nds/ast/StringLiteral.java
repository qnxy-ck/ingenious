package org.nds.ast;

/**
 * @author Qnxy
 * 2023/10/13
 */
public record StringLiteral(
        String value,
        String rawValue
) implements ASTree {

}
