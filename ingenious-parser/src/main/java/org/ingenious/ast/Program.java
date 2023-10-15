package org.ingenious.ast;

import java.util.List;

/**
 * AST入口节点
 *
 * @author Qnxy
 */
public record Program(
        List<ASTree> body
) implements ASTree {
}
