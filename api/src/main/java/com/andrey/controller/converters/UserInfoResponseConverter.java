package com.andrey.controller.converters;

import com.andrey.controller.responses.UserInfoResponse;
import com.andrey.db_entities.chat_user.ChatUser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserInfoResponseConverter implements Converter<ChatUser, UserInfoResponse> {

    private final ProfileInfoPartialResponseConverter profileConverter;

    @Override
    public UserInfoResponse convert(ChatUser source) {
        UserInfoResponse res = new UserInfoResponse();
        res.setUserId(source.getId());
        res.setUsername(source.getUserName());
        res.setProfiles(source.getOwnedProfiles().stream()
                .map(profileConverter::convert)
                .collect(Collectors.toList())
        );
        return res;
    }
}
