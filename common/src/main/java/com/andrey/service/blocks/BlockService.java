package com.andrey.service.blocks;

import com.andrey.db_entities.chat_user.ChatUser;

public interface BlockService {

    boolean fetchAndCheckIfBLockIsPresent(ChatUser user1, ChatUser user2);
}
