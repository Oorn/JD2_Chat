package com.andrey.service.profile;

import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.db_entities.chat_user.ChatUser;

import java.util.List;
import java.util.Optional;


public interface ProfilesService {

    Optional<ChatProfile> createNewProfile(ChatProfile request, ChatUser authUser);
    Optional<ChatProfile> updateProfile(ChatProfile request, ChatUser authUser);
    Optional<ChatProfile> deleteProfile(Long deleteId, ChatUser authUser);

    boolean checkProfileVisibility(ChatProfile profile, ChatUser viewingUser);

    List<ChatProfile> getOwnedProfiles(ChatUser authUser);

    List<ChatProfile> getRandomMatchmakingProfiles(ChatUser authUser, int amount);
}
