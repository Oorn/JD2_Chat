package com.andrey.controller.converters;

import com.andrey.controller.requests.profile_requests.CreateProfileRequest;
import com.andrey.controller.responses.ProfileInfoFullResponse;
import com.andrey.db_entities.chat_profile.ChatProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileInfoFullResponseConverter implements Converter<ChatProfile, ProfileInfoFullResponse> {
    @Override
    public ProfileInfoFullResponse convert(ChatProfile source) {
        return ProfileInfoFullResponse.builder()
                .profileDescription(source.getProfileDescription())
                .profileId(source.getId())
                .formatVersion(source.getFormatVersion())
                .profileName(source.getProfileName())
                .visibilityUserInfo(source.getProfileVisibilityUserInfo())
                .visibilityMatchmaking(source.getProfileVisibilityMatchmaking())
                .build();
    }
}
