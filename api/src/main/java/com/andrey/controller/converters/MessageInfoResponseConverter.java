package com.andrey.controller.converters;

import com.andrey.controller.responses.MessageInfoResponse;
import com.andrey.db_entities.chat_message.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageInfoResponseConverter implements Converter<ChatMessage, MessageInfoResponse> {
    @Override
    public MessageInfoResponse convert(ChatMessage source) {
        return MessageInfoResponse.builder()
                .messageId(source.getId())
                .formatVersion(source.getFormatVersion())
                .messageBody(source.getMessageBody())
                .status(source.getStatus())
                .lastUpdateDate(source.getLastUpdateDate())
                .build();
    }
}
