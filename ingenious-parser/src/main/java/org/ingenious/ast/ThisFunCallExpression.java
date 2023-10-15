package org.ingenious.ast;

import java.util.List;

/**
 * @author Qnxy
 */
public record ThisFunCallExpression(
        String funName,
        List<ASTree> arguments
) implements ASTree {
}
