package com.linhx.sso.controller.dtos.response;

import com.linhx.exceptions.message.Message;
import com.linhx.exceptions.message.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * @author linhx
 * @since 27/10/2020
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MessageDto {
    private MessageType type;
    private String messageCode;
    private Map<String, Object> params;

    public static MessageDto fromMessage(Message message) {
        return new MessageDto(message.getType(), message.getMessageCode(), message.getParams());
    }
}
