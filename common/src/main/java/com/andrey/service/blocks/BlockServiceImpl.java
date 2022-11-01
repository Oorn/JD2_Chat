package com.andrey.service.blocks;

import com.andrey.db_entities.chat_block.BlockStatus;
import com.andrey.db_entities.chat_block.ChatBlock;
import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.exceptions.BadTargetException;
import com.andrey.exceptions.IllegalStateException;
import com.andrey.exceptions.InteractionWithSelfException;
import com.andrey.exceptions.NoPermissionException;
import com.andrey.exceptions.NoSuchEntityException;
import com.andrey.exceptions.RemovedEntityException;
import com.andrey.repository.ChatBlockRepository;
import com.andrey.repository.ChatUserRepository;
import com.andrey.service.user.ChatUserUtilsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockServiceImpl implements BlockService{

    private final ChatBlockRepository blockRepository;

    private final ChatUserRepository userRepository;

    private final ChatUserUtilsService userUtils;

    private final EntityManager entityManager;

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

    @Override
    public Optional<ChatBlock> createBlock(ChatUser authUser, long targetUserId) {

        if (authUser.getId().equals(targetUserId))
            throw new InteractionWithSelfException("cannot block yourself");

        Optional<ChatUser> optionalTargetUser = userRepository.findChatUserByIdWithBlocks(targetUserId);
        if (optionalTargetUser.isEmpty())
            throw new NoSuchEntityException("user " + targetUserId + " does not exist");
        ChatUser targetUser = optionalTargetUser.get();
        if (!targetUser.isInteractable())
            throw new RemovedEntityException("user " + targetUserId + " has been removed");

        if (authUser.getBlocks().containsKey(targetUserId)) {
            //duping fix
            entityManager.merge(authUser);

            ChatBlock authBlock = authUser.getBlocks().get(targetUserId);
            ChatBlock targetBlock = targetUser.getBlocks().get(authUser.getId());
            switch (authBlock.getStatus()) {
                case ACTIVE:
                    //block is already present
                    throw new BadTargetException("user " + targetUserId + " is already blocked by user " + authUser.getId());
                case MIRRORED:
                    //user is already blocked by target
                    authBlock.setStatus(BlockStatus.ACTIVE);
                    blockRepository.saveAndFlush(authBlock);
                    return Optional.of(authBlock);
                case REMOVED:
                    //user and target used to block each other, but not anymore
                    authBlock.setStatus(BlockStatus.ACTIVE);
                    targetBlock.setStatus(BlockStatus.MIRRORED);

                    List<ChatBlock> blocksToSave = new ArrayList<>();
                    blocksToSave.add(authBlock);
                    blocksToSave.add(targetBlock);
                    blockRepository.saveAllAndFlush(blocksToSave);
                    return  Optional.of(authBlock);
                default:
                    break;
            }
        }

        //create new blocks
        ChatBlock directBlock = createNewBlockNoCheck(authUser, targetUser);
        return Optional.of(directBlock);
    }

    private ChatBlock createNewBlockNoCheck(ChatUser blockingUser, ChatUser blockedUser) {
        ChatBlock directBlock = ChatBlock.builder()
                .blockedUser(blockedUser)
                .blockedUserId(blockedUser.getId())
                .blockingUser(blockingUser)
                .status(BlockStatus.ACTIVE)
                .build();
        ChatBlock inverseBlock = ChatBlock.builder()
                .blockedUser(blockingUser)
                .blockedUserId(blockingUser.getId())
                .blockingUser(blockedUser)
                .status(BlockStatus.MIRRORED)
                .build();

        List<ChatBlock> blocksToSave = new ArrayList<>();
        blocksToSave.add(directBlock);
        blocksToSave.add(inverseBlock);

        blockRepository.saveAllAndFlush(blocksToSave);
        return directBlock;
    }

    @Override
    public Optional<ChatBlock> removeBlock(ChatUser authUser, long targetUserId) {

        if (!authUser.getBlocks().containsKey(targetUserId))
            throw new NoSuchEntityException("user " + targetUserId + " is not blocked by user " + authUser.getId());

        Optional<ChatUser> optionalTargetUser = userRepository.findChatUserByIdWithBlocks(targetUserId);
        if (optionalTargetUser.isEmpty())
            throw new IllegalStateException("user " + targetUserId + " exists in ChatBlocks, but not ChatUsers");
        ChatUser targetUser = optionalTargetUser.get();

        //dupe block fix, dirty but works
        //authUser = userRepository.findChatUserByIdWithBlocks(authUser.getId()).get();
        //dupe block fix 2, a bit cleaner
        entityManager.merge(authUser);

        ChatBlock authBlock = authUser.getBlocks().get(targetUserId);
        ChatBlock targetBlock = targetUser.getBlocks().get(authUser.getId());

        if (!authBlock.getStatus().equals(BlockStatus.ACTIVE))
            throw new BadTargetException("user " + targetUserId + " is not blocked by user " + authUser.getId());

        switch (targetBlock.getStatus()) {
            case ACTIVE:
                //inverse block is present
                authBlock.setStatus(BlockStatus.MIRRORED);
                blockRepository.saveAndFlush(authBlock);
                break;
            case MIRRORED:
                //inverse block is not present
                authBlock.setStatus(BlockStatus.REMOVED);
                targetBlock.setStatus(BlockStatus.REMOVED);

                List<ChatBlock> blocksToSave = new ArrayList<>();
                blocksToSave.add(authBlock);
                blocksToSave.add(targetBlock);

                blockRepository.saveAllAndFlush(blocksToSave);
                break;
            case REMOVED:
                //impossible case, one block is ACTIVE, other is REMOVED
                throw new IllegalStateException("blocks between users " + authUser.getId() + " and " + targetUserId + " are in invalid state: ACTIVE and REMOVED");
            default:
                break;
        }

        return Optional.of(authBlock);

    }

    @Override
    public Map<Long, ChatBlock> getAuthUserBlocks(ChatUser authUser) {
        return authUser.getBlocks().entrySet().stream()
                .filter(e -> !e.getValue().getStatus().equals(BlockStatus.REMOVED))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
