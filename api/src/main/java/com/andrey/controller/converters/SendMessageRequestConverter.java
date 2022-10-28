package com.andrey.controller.converters;

import com.andrey.controller.requests.message_requests.SendMessageRequest;
import com.andrey.controller.requests.profile_requests.UpdateProfileRequest;
import com.andrey.db_entities.chat_message.ChatMessage;
import com.andrey.db_entities.chat_message.MessageStatus;
import com.andrey.db_entities.chat_profile.ChatProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SendMessageRequestConverter implements Converter<SendMessageRequest, ChatMessage> {
    @Override
    public ChatMessage convert(SendMessageRequest source) {
        return ChatMessage.builder()
                .formatVersion(source.getFormatVersion())
                .messageBody(source.getMessageBody())
                .status(MessageStatus.ACTIVE)
                .build();
    }
}
