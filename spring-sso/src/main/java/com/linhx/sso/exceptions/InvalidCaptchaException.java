package com.linhx.sso.exceptions;

/**
 * Thrown if invalid captcha
 *
 * @author linhx
 */
public class InvalidCaptchaException extends org.springframework.security.core.AuthenticationException {
    public InvalidCaptchaException(String msg, Throwable t) {
        super(msg, t);
    }

    public InvalidCaptchaException(String msg) {
        super(msg);
    }
}
