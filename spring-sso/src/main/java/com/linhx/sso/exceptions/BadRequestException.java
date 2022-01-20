package com.linhx.sso.exceptions;

import com.linhx.exceptions.BaseException;

/**
 * BadRequestException
 *
 * @author linhx
 * @since 19/11/2021
 */
public class BadRequestException extends BaseException {
    public BadRequestException(String msg) {
        super(msg);
    }
}
