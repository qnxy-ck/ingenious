package org.nds.ast;

import java.util.List;

/**
 * @author Qnxy
 * 2023/10/13
 */
public record DollarFunCallExpression(
        String funName,
        List<ASTree> arguments
) implements ASTree {
}
