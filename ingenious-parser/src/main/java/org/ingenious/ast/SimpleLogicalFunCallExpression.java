package org.ingenious.ast;

/**
 * :username?
 * :username::funcName()?
 * <p>
 * 该表达式等同于
 * :username?.notNull()
 * :username::funcName()?.notNull()
 *
 * @author Qnxy
 */
public record SimpleLogicalFunCallExpression(
        ASTree callee
) implements ASTree {
}
