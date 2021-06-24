package com.linhx.utils.functions;

/**
 * @author nguyendinhlinhx
 */
public interface F<T, R> {
    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t) throws Exception;
}
