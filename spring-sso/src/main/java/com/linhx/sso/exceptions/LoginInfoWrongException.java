package com.linhx.sso.exceptions;

import com.linhx.exceptions.message.Message;

/**
 * Thrown if cannot find login info (username, password) in the request body.
 *
 * @author linhx
 */
public class LoginInfoWrongException extends CAuthenticationException {
    public LoginInfoWrongException(String msg, Throwable t) {
        super(msg, t);
    }

    public LoginInfoWrongException(String msg) {
        super(msg);
    }

    public LoginInfoWrongException(Message msg) {
        super(msg);
    }
}
