package com.andrey.controller.converters;

import com.andrey.controller.requests.profile_requests.UpdateProfileRequest;
import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.exceptions.NoSuchEntityException;
import com.andrey.repository.ChatProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UpdateProfileRequestConverter implements Converter<UpdateProfileRequest, ChatProfile> {
    private final ChatProfileRepository profileRepository;

    private final EntityManager entityManager;

    @Override
    public ChatProfile convert(UpdateProfileRequest source) {

        Optional<ChatProfile> oldProfile = profileRepository.findChatProfileByIdWithOwner(source.getId());
        if (oldProfile.isEmpty())
            throw new NoSuchEntityException("profile with id " + source.getId() + " does not exist");
        entityManager.detach(oldProfile.get());

        oldProfile.get().setProfileName(source.getProfileName());
        oldProfile.get().setProfileDescription(source.getProfileDescription());
        oldProfile.get().setFormatVersion(source.getFormatVersion());
        oldProfile.get().setProfileVisibilityMatchmaking(source.getVisibilityMatchmaking());
        oldProfile.get().setProfileVisibilityUserInfo(source.getVisibilityUserInfo());

        return oldProfile.get();
    }


}