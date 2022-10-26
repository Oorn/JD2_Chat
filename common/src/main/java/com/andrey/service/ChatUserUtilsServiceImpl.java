package com.andrey.service;

import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.db_entities.chat_user.ChatUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatUserUtilsServiceImpl implements ChatUserUtilsService{
    @Override
    public long getActiveProfileNumber(ChatUser user) {
        return user.getOwnedProfiles().stream()
                .filter(ChatProfile::isInteractable)
                .count();
    }
}
