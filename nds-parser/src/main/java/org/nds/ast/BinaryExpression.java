package org.nds.ast;

import org.nds.token.OperatorToken;

/**
 * @author Qnxy
 * 2023/10/13
 */
public record BinaryExpression(
        OperatorToken operator,
        ASTree left,
        ASTree right
) implements ASTree {
}
