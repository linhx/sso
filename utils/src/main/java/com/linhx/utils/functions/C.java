package com.linhx.utils.functions;

/**
 * @author linhx
 * @since 08/10/2020
 */
public interface C<T> {
    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void accept(T t) throws Exception;
}
