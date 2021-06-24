package com.linhx.exceptions.message;

/**
 * MessageType
 *
 * @author linhx
 * @date 28/05/2020
 */
public enum MessageType {
    ERROR(1), INFO(2);

    private int value;

    MessageType(int value) {
        this.value = value;
    }
}
