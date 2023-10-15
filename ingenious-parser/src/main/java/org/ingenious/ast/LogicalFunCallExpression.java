package org.ingenious.ast;

import java.util.List;

/**
 * ?.
 * <p>
 * :username?.funcName()
 * :username::funcName1()?.logicalFuncName()
 *
 * @author Qnxy
 */
public record LogicalFunCallExpression(
        ASTree callee,
        String funName,
        List<ASTree> arguments
) implements ASTree {
}
