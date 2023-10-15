package org.ingenious.token;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * token信息
 *
 * @author Qnxy
 */
public interface Token<V> {

    @JsonValue
    V value();

    TokenLocation tokenLocation();

}