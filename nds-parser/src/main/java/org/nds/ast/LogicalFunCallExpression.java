package org.nds.ast;

import java.util.List;

/**
 * ?.
 * <p>
 * :username?.funcName()
 * :username::funcName1()?.logicalFuncName()
 *
 * @author Qnxy
 * 2023/10/13
 */
public record LogicalFunCallExpression(
        ASTree callee,
        String funName,
        List<ASTree> arguments
) implements ASTree {
}
