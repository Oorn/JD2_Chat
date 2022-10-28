package com.andrey.repository;

import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_message.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends CrudRepository<ChatMessage, Long>
        , JpaRepository<ChatMessage, Long>
        , PagingAndSortingRepository<ChatMessage, Long> {

    ChatMessage findChatMessageById(Long id);


    @Query(value = "select m from ChatMessage m" +
            " left join fetch m.channel c" +
            " left join fetch c.members mem" +
            " left join fetch mem.userProfile p" +
            " where m.id = :id ")
    ChatMessage findChatMessageByIdWithChannelWithMembersWithUsers(Long id);
}
