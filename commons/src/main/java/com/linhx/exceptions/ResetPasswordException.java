package com.linhx.exceptions;

import com.linhx.exceptions.message.Message;

import java.util.List;

/**
 * ResetPasswordException
 *
 * @author linhx
 * @since 09/04/2020
 */
public class ResetPasswordException extends BusinessException {
    public ResetPasswordException(String message) {
        super(Message.error(message).build());
    }

    public ResetPasswordException(Throwable t) {
        super(t);
    }

    public ResetPasswordException(Message... message) {
        super(message);
    }

    public ResetPasswordException(List<Message> message) {
        super(message);
    }
}
