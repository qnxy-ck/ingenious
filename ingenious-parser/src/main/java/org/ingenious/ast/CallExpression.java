package org.ingenious.ast;

import java.util.List;

/**
 * :username::funcName()
 *
 * @author Qnxy
 */
public record CallExpression(
        ASTree caller,
        String funName,
        List<ASTree> arguments
) implements ASTree {
}
