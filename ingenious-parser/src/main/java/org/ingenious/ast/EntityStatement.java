package org.ingenious.ast;

import java.util.List;

/**
 * @author Qnxy
 */
public record EntityStatement(
        List<Identifier> values
) implements ASTree {
}
