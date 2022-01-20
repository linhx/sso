package com.linhx.sso.exceptions;

/**
 * Thrown if invalid captcha
 *
 * @author linhx
 */
public class InvalidLoginCaptchaException extends org.springframework.security.core.AuthenticationException {
    public InvalidLoginCaptchaException(String msg, Throwable t) {
        super(msg, t);
    }

    public InvalidLoginCaptchaException(String msg) {
        super(msg);
    }
}
