package org.ingenious.ast;

/**
 * this::funcName()
 *
 * @author Qnxy
 */
public record ThisExpression() implements ASTree {

    public static final ThisExpression INSTANCE = new ThisExpression();
    
}
