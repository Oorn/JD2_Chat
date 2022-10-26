package com.andrey.service.profile;

import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.db_entities.chat_user.ChatUserRepository;

import java.util.Optional;


public interface ProfilesService {

    Optional<ChatProfile> createNewProfile(ChatProfile request, ChatUser user);
    Optional<ChatProfile> updateProfile(ChatProfile request, ChatUser user);
    Optional<ChatProfile> deleteProfile(Long deleteId, ChatUser user);
}
