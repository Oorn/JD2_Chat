package com.andrey.service.profile;

import com.andrey.Constants;
import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.db_entities.chat_profile.ChatProfileRepository;
import com.andrey.db_entities.chat_profile.ProfileStatus;
import com.andrey.db_entities.chat_profile.ProfileVisibilityMatchmaking;
import com.andrey.db_entities.chat_profile.ProfileVisibilityUserInfo;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.db_entities.chat_user.ChatUserRepository;
import com.andrey.service.ChatUserUtilsService;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfilesServiceImpl implements ProfilesService{

    private final ChatUserRepository userRepository;

    private final ChatProfileRepository profileRepository;

    private final ChatUserUtilsService userUtils;

    @Override
    public Optional<ChatProfile> createNewProfile(ChatProfile newProfile, ChatUser user) {

        if (newProfile.getProfileVisibilityMatchmaking() == ProfileVisibilityMatchmaking.DEFAULT_PROFILE_SEARCH_VISIBILITY)
            return Optional.empty();
        if (newProfile.getProfileVisibilityUserInfo() == ProfileVisibilityUserInfo.DEFAULT_PROFILE_USER_INFO_VISIBILITY)
            return Optional.empty();

        ChatUser persistUser = userRepository.findChatUserById(user.getId());
        if (userUtils.getActiveProfileNumber(persistUser) >= Constants.MAX_PROFILES_PER_USER)
            return Optional.empty();

        newProfile.setOwner(user);

        newProfile = profileRepository.saveAndFlush(newProfile);
        return Optional.of(newProfile);
    }

    @Override
    public Optional<ChatProfile> updateProfile(ChatProfile newProfile, ChatUser user) {
        if (newProfile == null)
            return Optional.empty();
        if (!newProfile.isInteractable())
            return Optional.empty();
        if (newProfile.getProfileVisibilityMatchmaking() == ProfileVisibilityMatchmaking.DEFAULT_PROFILE_SEARCH_VISIBILITY)
            return Optional.empty();
        if (newProfile.getProfileVisibilityUserInfo() == ProfileVisibilityUserInfo.DEFAULT_PROFILE_USER_INFO_VISIBILITY)
            return Optional.empty();

        if (!newProfile.getOwner().getId().equals(user.getId()))
            return Optional.empty();

        //Optional<ChatProfile> oldProfile = profileRepository.findChatProfileByIdWithOwner(newProfile.getId());
        //if (oldProfile.isEmpty())
        //    return Optional.empty();
        //if (!oldProfile.get().getOwner().getId().equals(user.getId()))
        //    return Optional.empty();

        //newProfile.setOwner(user);

        newProfile = profileRepository.saveAndFlush(newProfile);
        return Optional.of(newProfile);

    }

    @Override
    public Optional<ChatProfile> deleteProfile(Long deleteId, ChatUser user) {
        Optional<ChatProfile> optionalOldProfile = profileRepository.findChatProfileByIdWithOwner(deleteId);
        if (optionalOldProfile.isEmpty())
            return Optional.empty();
        ChatProfile oldProfile = optionalOldProfile.get();
        if (!oldProfile.isInteractable())
            return Optional.empty();
        if (!oldProfile.getOwner().getId().equals(user.getId()))
            return Optional.empty();

        oldProfile.setStatus(ProfileStatus.REMOVED);
        return Optional.of(oldProfile);
    }
}
