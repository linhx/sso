package com.linhx.sso.exceptions;

/**
 * Thrown if cannot find login info (username, password) in the request body.
 *
 * @author linhx
 */
public class LoginInfoWrongException extends org.springframework.security.core.AuthenticationException {
    public LoginInfoWrongException(String msg, Throwable t) {
        super(msg, t);
    }

    public LoginInfoWrongException(String msg) {
        super(msg);
    }
}
