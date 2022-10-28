package com.andrey.controller.converters;

import com.andrey.controller.responses.ChatFromProfileUserInfoResponse;
import com.andrey.controller.responses.UserInfoShortResponse;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import com.andrey.db_entities.chat_user.ChatUser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatFromProfileUserInfoResponseConverter  implements Converter<ChatChannelMembership, ChatFromProfileUserInfoResponse> {

    private final UserInfoShortResponseConverter userInfoConverter;
    private final ProfileInfoPartialResponseConverter profileConverter;

    @Override
    public ChatFromProfileUserInfoResponse convert(ChatChannelMembership source) {
        ChatFromProfileUserInfoResponse res = new ChatFromProfileUserInfoResponse();
        res.setUser(userInfoConverter.convert(source.getUser()));
        res.setProfile(profileConverter.convert(source.getUserProfile()));
        return res;
    }
}
