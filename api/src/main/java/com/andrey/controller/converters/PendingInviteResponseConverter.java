package com.andrey.controller.converters;

import com.andrey.controller.responses.PendingInviteResponse;
import com.andrey.db_entities.chat_channel_invite.ChatChannelInvite;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PendingInviteResponseConverter implements Converter<ChatChannelInvite, PendingInviteResponse> {
    private final UserInfoShortResponseConverter userInfoConverter;
    @Override
    public PendingInviteResponse convert(ChatChannelInvite source) {
        return PendingInviteResponse.builder()
                .inviteId(source.getId())
                .sender(userInfoConverter.convert(source.getSender()))
                .channelName(source.getChannel().getChannelName())
                .build();
    }
}
