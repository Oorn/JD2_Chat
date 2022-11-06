package com.andrey.service.cached_user_details;

import com.andrey.db_entities.chat_user.ChatUser;

public interface CachedUserDetailsService {
    ChatUser cachedFindChatUserByEmailWithFriendshipsAndChatMembershipsAndBlocks (String email);
    void evictUserFromCache (ChatUser user);
}
