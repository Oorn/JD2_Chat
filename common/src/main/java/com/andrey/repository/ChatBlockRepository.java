package com.andrey.repository;

import com.andrey.db_entities.chat_block.ChatBlock;
import com.andrey.db_entities.chat_user.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatBlockRepository extends CrudRepository<ChatBlock, Long>
        , JpaRepository<ChatBlock, Long>
        , PagingAndSortingRepository<ChatBlock, Long> {


    Optional<ChatBlock> findChatBlockByBlockingUserAndBlockedUser (ChatUser blockingUser, ChatUser blockedUser);
}
