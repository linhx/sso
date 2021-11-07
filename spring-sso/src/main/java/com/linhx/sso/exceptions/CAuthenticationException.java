package com.linhx.sso.exceptions;

import com.linhx.exceptions.message.Message;

/**
 * CAuthenticationException
 *
 * @author linhx
 * @since 07/11/2021
 */
public class CAuthenticationException extends org.springframework.security.core.AuthenticationException {
    final Message message;

    public Message getCMessage() {
        return this.message;
    }

    public CAuthenticationException(String msg, Throwable t) {
        super(msg, t);
        this.message = Message.error(msg).build();
    }

    public CAuthenticationException(String msg) {
        super(msg);
        this.message = Message.error(msg).build();
    }

    public CAuthenticationException(Message msg) {
        super(msg.getMessageCode());
        this.message = msg;
    }
}
