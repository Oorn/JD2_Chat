package com.andrey.db_entities.chat_message;

import com.andrey.db_entities.chat_channel.ChatChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends CrudRepository<ChatMessage, Long>
        , JpaRepository<ChatMessage, Long>
        , PagingAndSortingRepository<ChatMessage, Long> {

    ChatMessage findChatMessageById(Long id);
}
