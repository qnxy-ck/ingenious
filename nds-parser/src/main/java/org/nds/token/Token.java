package org.nds.token;

/**
 * token信息
 *
 * @author Qnxy
 * 2023/10/12
 */
public interface Token<V> {

    V value();

    TokenLocation tokenLocation();

}