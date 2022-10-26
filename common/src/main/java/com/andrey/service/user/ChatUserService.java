package com.andrey.service.user;

import com.andrey.db_entities.chat_user.ChatUser;

import java.util.Optional;

public interface ChatUserService {
    Optional<ChatUser> findChatUserByIdWithProfiles (Long id);

    Optional<ChatUser> getUserInfoForViewer(Long userId, ChatUser viewer);

}
