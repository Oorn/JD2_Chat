package com.andrey.controller.converters;

import com.andrey.controller.responses.ProfileInfoFullResponse;
import com.andrey.controller.responses.ProfileInfoPartialResponse;
import com.andrey.db_entities.chat_profile.ChatProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileInfoPartialResponseConverter implements Converter<ChatProfile, ProfileInfoPartialResponse> {
    @Override
    public ProfileInfoPartialResponse convert(ChatProfile source) {
        return ProfileInfoPartialResponse.builder()
                .profileDescription(source.getProfileDescription())
                .profileId(source.getId())
                .formatVersion(source.getFormatVersion())
                .profileName(source.getProfileName())
                .build();
    }
}
