package com.andrey.service.blocks;

import com.andrey.db_entities.chat_block.BlockStatus;
import com.andrey.db_entities.chat_block.ChatBlock;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.repository.ChatBlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlockServiceImpl implements BlockService{

    private final ChatBlockRepository blockRepository;

    public boolean fetchAndCheckIfBLockIsPresent(ChatUser user1, ChatUser user2) {

        Optional<ChatBlock> optionalBlock = blockRepository.findChatBlockByBlockingUserAndBlockedUser(user1, user2);
        if (optionalBlock.isEmpty())
            return false;
        ChatBlock block = optionalBlock.get();
        if (block.getStatus().equals(BlockStatus.ACTIVE))
            return true;
        if (block.getStatus().equals(BlockStatus.MIRRORED))
            return true;

        return false;
    }
}
