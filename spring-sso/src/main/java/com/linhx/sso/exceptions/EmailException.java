package com.linhx.sso.exceptions;

import com.linhx.exceptions.BusinessException;

/**
 * EmailException
 *
 * @author linhx
 * @since 08/09/2021
 */
public class EmailException extends BusinessException {
    public EmailException(String message) {
        super(message);
    }
    public EmailException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
