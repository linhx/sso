package com.linhx.exceptions.message;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Message
 *
 * @author linhx
 * @date 28/05/2020
 */
@Getter
public class Message {
    private MessageType type;
    private String messageCode;
    private Map<String, Object> params;

    private Message (MessageBuilder builder) {
        this.type = builder.getType();
        this.messageCode = builder.getMessageCode();
        this.params = builder.getParams();
    }

    public static MessageBuilder builder (String messageCode) {
        return new MessageBuilder(messageCode);
    }

    public static ErrorBuilder error (String messageCode) {
        return new ErrorBuilder(messageCode);
    }

    public static InfoBuilder info (String messageCode) {
        return new InfoBuilder(messageCode);
    }

    @Getter
    public static class MessageBuilder {
        private final String messageCode;
        private MessageType type = MessageType.INFO;
        private Map<String, Object> params;

        protected MessageBuilder (String messageCode) {
            this.messageCode = messageCode;
        }

        public MessageBuilder type (MessageType type) {
            this.type = type;
            return this;
        }

        public MessageBuilder params (Map<String, Object> params) {
            this.params = params;
            return this;
        }

        public MessageBuilder param (String name, Object value) {
            if (this.params == null) {
                this.params = new HashMap<>();
            }
            this.params.put(name, value);
            return this;
        }

        public Message build () {
            return new Message(this);
        }
    }

    @Getter
    public static class ErrorBuilder extends MessageBuilder {
        private final MessageType type = MessageType.ERROR;

        protected ErrorBuilder(String messageCode) {
            super(messageCode);
        }
    }

    @Getter
    public static class InfoBuilder extends MessageBuilder {
        private final MessageType type = MessageType.INFO;

        protected InfoBuilder(String messageCode) {
            super(messageCode);
        }
    }
}
