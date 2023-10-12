package org.nds.ast;

import java.util.List;

/**
 * 查询方法块声明
 *
 * @author Qnxy
 * 2023/10/13
 */
public record SearchFunBlockStatement(
        List<ASTree> body
) implements ASTree {
}
