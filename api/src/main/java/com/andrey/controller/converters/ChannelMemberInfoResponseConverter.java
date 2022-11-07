package com.andrey.controller.converters;


import com.andrey.controller.responses.ChannelMemberInfoResponse;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelMemberInfoResponseConverter implements Converter<ChatChannelMembership, ChannelMemberInfoResponse> {

    private final UserInfoShortResponseConverter userConverter;

    @Override
    public ChannelMemberInfoResponse convert(ChatChannelMembership source) {
        return ChannelMemberInfoResponse.builder()
                .user(userConverter.convert(source.getUser()))
                .role(source.getRole())
                .build();
    }
}
