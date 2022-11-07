package com.andrey.controller.converters;

import com.andrey.controller.requests.channel_requests.UpdateMultiuserChannelRequest;
import com.andrey.db_entities.chat_channel.ChannelStatus;
import com.andrey.db_entities.chat_channel.ChannelType;
import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.exceptions.BadEnumValueException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateMultiuserChannelRequestConverter implements Converter<UpdateMultiuserChannelRequest, ChatChannel> {
    @Override
    public ChatChannel convert(UpdateMultiuserChannelRequest source) {
        switch (source.getDefaultRole()) {
            case READ_WRITE_INVITE_ACCESS:
            case READ_WRITE_ACCESS:
            case READ_ACCESS:
                break;
            default:
                throw new BadEnumValueException("default role must be one of READ_WRITE_INVITE_ACCESS, READ_WRITE_ACCESS, READ_ACCESS");
        }

        return ChatChannel.builder()
                .id(source.getChannelId())
                .channelName(source.getChannelName())
                .channelType(ChannelType.MULTI_USER_CHANNEL)
                .status(ChannelStatus.ACTIVE)
                .defaultRole(source.getDefaultRole())
                .build();

    }
}
