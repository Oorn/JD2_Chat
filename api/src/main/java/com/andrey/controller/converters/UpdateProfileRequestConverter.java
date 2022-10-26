package com.andrey.controller.converters;

import com.andrey.Constants;
import com.andrey.controller.requests.profile_requests.CreateProfileRequest;
import com.andrey.controller.requests.profile_requests.UpdateProfileRequest;
import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.db_entities.chat_profile.ChatProfileRepository;
import com.andrey.db_entities.chat_profile.ProfileStatus;
import com.andrey.db_entities.chat_user.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UpdateProfileRequestConverter implements Converter<UpdateProfileRequest, ChatProfile> {
    private final ChatProfileRepository profileRepository;

    private final EntityManager entityManager;

    @Override
    public ChatProfile convert(UpdateProfileRequest source) {

        Optional<ChatProfile> oldProfile = profileRepository.findChatProfileByIdWithOwner(source.getId());
        if (oldProfile.isEmpty())
            return null;
        entityManager.detach(oldProfile.get());

        oldProfile.get().setProfileName(source.getProfileName());
        oldProfile.get().setProfileDescription(source.getProfileDescription());
        oldProfile.get().setFormatVersion(source.getFormatVersion());
        oldProfile.get().setProfileVisibilityMatchmaking(source.getVisibilityMatchmaking());
        oldProfile.get().setProfileVisibilityUserInfo(source.getVisibilityUserInfo());

        return oldProfile.get();
    }
/*
    @Autowired
    @Lazy
    private final CreateProfileRequestConverter createConverter;

    @Override
    public ChatProfile convert(UpdateProfileRequest source) {
        ChatProfile res = createConverter.convert(source);
        res.setId(source.getId());
        return res;
    }

 */

}