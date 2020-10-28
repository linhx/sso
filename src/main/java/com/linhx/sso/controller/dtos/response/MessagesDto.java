package com.linhx.sso.controller.dtos.response;

import com.linhx.exceptions.message.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author linhx
 * @since 27/10/2020
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MessagesDto {
    private List<MessageDto> messages;

    public static MessagesDto fromMessages(List<Message> messages) {
        var msgs = new MessagesDto();
        if (messages != null) {
            msgs.setMessages(messages.stream().map(MessageDto::fromMessage).collect(Collectors.toList()));
        }
        return msgs;
    }

    public static MessagesDto fromMessage(Message message) {
        var msgs = new MessagesDto();
        if (message != null) {
            msgs.setMessages(Collections.singletonList(MessageDto.fromMessage(message)));
        }
        return msgs;
    }
}
