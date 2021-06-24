package com.linhx.exceptions;

import com.linhx.exceptions.message.Message;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * BaseException
 *
 * @author linhx
 * @since 09/10/2020
 */
@Getter
public class BaseException extends Exception {

    private List<Message> messages;

    public BaseException () {
    }

    public BaseException(Throwable t) {
        super(t);
    }

    public BaseException(String msg, Throwable t) {
        super(msg, t);
    }

    public BaseException (String message) {
        super(message);
    }

    public BaseException(List<Message> messages) {
        this.messages = Collections.unmodifiableList(messages);
    }
    public BaseException(Message... messages) {
        this.messages = Arrays.asList(messages);
    }
}
