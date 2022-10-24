package com.andrey.controller.converters;

import com.andrey.Constants;
import com.andrey.controller.requests.ChatUserCreateRequest;
import com.andrey.controller.requests.profile_requests.CreateProfileRequest;
import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.db_entities.chat_profile.ProfileStatus;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.db_entities.chat_user.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreateProfileRequestConverter implements Converter<CreateProfileRequest, ChatProfile> {
    @Override
    public ChatProfile convert(CreateProfileRequest source) {
        return ChatProfile.builder()
                .profileName(source.getProfileName())
                .profileDescription(source.getProfileDescription())
                .formatVersion(source.getFormatVersion())
                .profileVisibilityMatchmaking(source.getVisibilityMatchmaking())
                .profileVisibilityUserInfo(source.getVisibilityUserInfo())
                .status(ProfileStatus.OK)
                .build();
    }
}
