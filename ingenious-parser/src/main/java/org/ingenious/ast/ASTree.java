package org.ingenious.ast;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * AST
 *
 * @author Qnxy
 */
@JsonPropertyOrder("type")
public interface ASTree {

    @JsonGetter
    default String type() {
        return this.getClass().getSimpleName();
    }
}
