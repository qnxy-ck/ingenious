package org.nds.token;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * token信息
 *
 * @author Qnxy
 * 2023/10/12
 */
public interface Token<V> {

    @JsonValue
    V value();

    TokenLocation tokenLocation();

}