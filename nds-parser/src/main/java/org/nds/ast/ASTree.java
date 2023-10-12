package org.nds.ast;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * AST
 *
 * @author Qnxy
 * 2023/10/12
 */
@JsonPropertyOrder("type")
public interface ASTree {

    @JsonGetter
    default String type() {
        return this.getClass().getSimpleName();
    }
}
