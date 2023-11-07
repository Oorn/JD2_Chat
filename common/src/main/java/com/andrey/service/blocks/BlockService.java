package com.andrey.service.blocks;

import com.andrey.db_entities.chat_block.ChatBlock;
import com.andrey.db_entities.chat_user.ChatUser;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BlockService {

    @Deprecated
    boolean fetchAndCheckIfBLockIsPresent(ChatUser user1, ChatUser user2);

    Optional<ChatBlock> createBlock(ChatUser authUser, long targetUserId);

    Optional<ChatBlock> removeBlock(ChatUser authUser, long targetUserId);




}
