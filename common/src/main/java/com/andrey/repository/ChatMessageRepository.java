package com.andrey.repository;

import com.andrey.Constants;
import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_message.ChatMessage;
import com.andrey.db_entities.chat_message.MessageStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

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
    Optional<ChatMessage> findChatMessageByIdWithChannelWithMembersWithUsers(Long id);

    @Query(value = "select m from ChatMessage m" +
            " left join fetch m.channel c" +
            " left join fetch m.sender u" +
            " where c.id = :channelId and m.status = :status")
    List<ChatMessage> findChatMessagesByChannelIdAndStatusWithUserAndChannel(@Param("channelId") long channelId, @Param("status") MessageStatus status, Pageable pageable);

    default List<ChatMessage> getLatestChatMessagesByChannelId(long channelId, int limit)
    {
        Pageable p = PageRequest.of(0, limit, Sort.by("id").descending());
        return findChatMessagesByChannelIdAndStatusWithUserAndChannel(channelId, MessageStatus.ACTIVE, p);
    }

    @Query(value = "select m from ChatMessage m" +
            " left join fetch m.channel c" +
            " left join fetch m.sender u" +
            " where c.id = :channelId and m.id < :before and m.status = :status")
    List<ChatMessage> findChatMessagesByChannelIdAndStatusBeforeMessageIdWithUserAndChannel(@Param("channelId") long channelId, @Param("status") MessageStatus status, @Param("before") long beforeMessageId, Pageable pageable);

    default List<ChatMessage> getChatMessagesBeforeMessageIdByChannelId(long channelId, long beforeMessageId, int limit)
    {
        Pageable p = PageRequest.of(0, limit, Sort.by("id").descending());
        return findChatMessagesByChannelIdAndStatusBeforeMessageIdWithUserAndChannel(channelId, MessageStatus.ACTIVE, beforeMessageId, p);
    }

    @Query(value = "select m from ChatMessage m" +
            " left join fetch m.channel c" +
            " left join fetch m.sender u" +
            " where c.id = :channelId and m.id > :after and m.status = :status")
    List<ChatMessage> findChatMessagesByChannelIdAndStatusAfterMessageIdWithUserAndChannel(@Param("channelId") long channelId, @Param("status") MessageStatus status, @Param("after") long afterMessageId, Pageable pageable);

    default List<ChatMessage> getChatMessagesAfterMessageIdByChannelId(long channelId, long afterMessageId, int limit)
    {
        Pageable p = PageRequest.of(0, limit, Sort.by("id").ascending());
        return findChatMessagesByChannelIdAndStatusAfterMessageIdWithUserAndChannel(channelId, MessageStatus.ACTIVE, afterMessageId, p);
    }

    @Query(value = "select m from ChatMessage m" +
            " left join fetch m.channel c" +
            " left join fetch m.sender u" +
            " where (c.id = :channelId and ((m.lastUpdateDate > :afterTimestamp)" +
                " or (m.lastUpdateDate = :afterTimestamp and m.id > :afterId)))")
    List<ChatMessage> findChatMessageUpdatesAfterMessageIdAndTimestampByChannelId(@Param("channelId") long channelId, @Param("afterId") long afterMessageId, @Param("afterTimestamp") Timestamp afterTimestamp, Pageable pageable);

    default List<ChatMessage> getChatUpdatesAfterMessageIdAndTimestampByChannelId(long channelId, long afterMessageId, Timestamp afterTimestamp, int limit)
    {
        Pageable p = PageRequest.of(0, limit, Sort.by("id").ascending());
        return findChatMessageUpdatesAfterMessageIdAndTimestampByChannelId(channelId, afterMessageId, afterTimestamp, p);
    }
}
