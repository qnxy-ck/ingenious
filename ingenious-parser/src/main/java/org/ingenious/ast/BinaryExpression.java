package org.ingenious.ast;

import org.ingenious.token.OperatorToken;

/**
 * @author Qnxy
 */
public record BinaryExpression(
        OperatorToken operator,
        ASTree left,
        ASTree right
) implements ASTree {
}
