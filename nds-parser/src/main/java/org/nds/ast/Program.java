package org.nds.ast;

import java.util.List;

/**
 * AST入口节点
 *
 * @author Qnxy
 * 2023/10/12
 */
public record Program(
        List<ASTree> body
) implements ASTree {
}
