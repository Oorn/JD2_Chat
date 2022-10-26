package com.andrey.repository;

import com.andrey.db_entities.chat_block.ChatBlock;
import com.andrey.db_entities.chat_channel.ChatChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatBlockRepository extends CrudRepository<ChatBlock, Long>
        , JpaRepository<ChatBlock, Long>
        , PagingAndSortingRepository<ChatBlock, Long> {

    ChatBlock findChatBlockById(Long id);
}
