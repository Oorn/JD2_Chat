package com.andrey.controller.converters;

import com.andrey.controller.responses.UserInfoResponse;
import com.andrey.controller.responses.UserInfoShortResponse;
import com.andrey.db_entities.chat_user.ChatUser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserInfoShortResponseConverter implements Converter<ChatUser, UserInfoShortResponse> {
    @Override
    public UserInfoShortResponse convert(ChatUser source) {
        UserInfoShortResponse res = new UserInfoShortResponse();
        res.setUserId(source.getId());
        res.setUsername(source.getUserName());
        return res;
    }
}
