package org.ingenious.ast;

import java.util.List;

/**
 * :username::funcName()
 *
 * @author Qnxy
 */
public record FunCallExpression(
        ASTree callee,
        String funName,
        List<ASTree> arguments
) implements ASTree {
}
