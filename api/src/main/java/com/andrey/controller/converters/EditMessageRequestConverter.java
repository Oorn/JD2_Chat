package com.andrey.controller.converters;

import com.andrey.controller.requests.message_requests.EditMessageRequest;
import com.andrey.db_entities.chat_message.ChatMessage;
import com.andrey.db_entities.chat_message.MessageStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EditMessageRequestConverter implements Converter<EditMessageRequest, ChatMessage> {
    @Override
    public ChatMessage convert(EditMessageRequest source) {
        return ChatMessage.builder()
                .id(source.getMessageId())
                .formatVersion(source.getFormatVersion())
                .messageBody(source.getMessageBody())
                .status(MessageStatus.ACTIVE)
                .build();
    }
}
