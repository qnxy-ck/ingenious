package org.ingenious.ast;

import java.util.List;

/**
 * 查询方法块声明
 *
 * @author Qnxy
 */
public record SearchFunBlockStatement(
        List<ASTree> body
) implements ASTree {
}
