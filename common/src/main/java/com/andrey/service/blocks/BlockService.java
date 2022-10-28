package com.andrey.service.blocks;

import com.andrey.db_entities.chat_user.ChatUser;

public interface BlockService {

    @Deprecated
    boolean fetchAndCheckIfBLockIsPresent(ChatUser user1, ChatUser user2);
}
