package com.linhx.exceptions;

import com.linhx.exceptions.message.Message;

import java.util.List;

/**
 * ResourceNotFoundException
 *
 * @author linhx
 * @since 10/08/2021
 */
public class ResourceNotFoundException extends BaseException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(Message.error(message).build());
    }
    public ResourceNotFoundException(Message... message) {
        super(message);
    }
    public ResourceNotFoundException(List<Message> message) {
        super(message);
    }
}
