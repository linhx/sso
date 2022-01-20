package com.linhx.exceptions;

import com.linhx.exceptions.message.Message;

import java.util.List;

public class BusinessException extends BaseException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public BusinessException() {
    }

    public BusinessException(Throwable t) {
        super(t);
    }

    public BusinessException(String message) {
        super(Message.error(message).build());
    }

    public BusinessException(String message, Throwable t) {
        super(message, t);
    }

    public BusinessException(Message... message) {
        super(message);
    }

    public BusinessException(List<Message> message) {
        super(message);
    }
}
