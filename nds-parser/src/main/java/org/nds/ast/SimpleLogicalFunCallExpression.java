package org.nds.ast;

/**
 * :username?
 * :username::funcName()?
 * <p>
 * 该表达式等同于
 * :username?.notNull()
 * :username::funcName()?.notNull()
 *
 * @author Qnxy
 * 2023/10/13
 */
public record SimpleLogicalFunCallExpression(
        ASTree callee
) implements ASTree {
}
