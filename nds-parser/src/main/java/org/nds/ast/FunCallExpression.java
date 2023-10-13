package org.nds.ast;

import java.util.List;

/**
 * :username::funcName()
 *
 * @author Qnxy
 * 2023/10/13
 */
public record FunCallExpression(
        ASTree callee,
        String funName,
        List<ASTree> arguments
) implements ASTree {
}
