package com.andrey.service.profile;

import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.db_entities.chat_profile.ChatProfileRepository;
import com.andrey.db_entities.chat_profile.ProfileVisibilityMatchmaking;
import com.andrey.db_entities.chat_profile.ProfileVisibilityUserInfo;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.db_entities.chat_user.ChatUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfilesServiceImpl implements ProfilesService{

    private final ChatUserRepository userRepository;

    private final ChatProfileRepository profileRepository;

    @Override
    public Optional<ChatProfile> createNewProfile(ChatProfile newProfile, String userEmail) {

        if (newProfile.getProfileVisibilityMatchmaking() == ProfileVisibilityMatchmaking.DEFAULT_PROFILE_SEARCH_VISIBILITY)
            return Optional.empty();
        if (newProfile.getProfileVisibilityUserInfo() == ProfileVisibilityUserInfo.DEFAULT_PROFILE_USER_INFO_VISIBILITY)
            return Optional.empty();

        ChatUser user = userRepository.findChatUserByEmail(userEmail);
        newProfile.setOwner(user);

        newProfile = profileRepository.saveAndFlush(newProfile);
        return Optional.of(newProfile);
    }
}
