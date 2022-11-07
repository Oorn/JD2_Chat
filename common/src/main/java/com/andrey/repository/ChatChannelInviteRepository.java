package com.andrey.repository;

import com.andrey.db_entities.chat_channel_invite.ChannelInviteStatus;
import com.andrey.db_entities.chat_channel_invite.ChatChannelInvite;
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
public interface ChatChannelInviteRepository extends CrudRepository<ChatChannelInvite, Long>
        , JpaRepository<ChatChannelInvite, Long>
        , PagingAndSortingRepository<ChatChannelInvite, Long> {

    @Query(value = "select i from ChatChannelInvite i" +
            " left join fetch i.targetUser t" +
            " left join fetch i.sender s" +
            " left join fetch i.channel c" +
            " where i.id = :id")
    Optional<ChatChannelInvite> findChatChannelInviteByIdWithSenderAndTargetAndChannel(Long id);


    @Query(value = "select i from ChatChannelInvite i" +
            " left join fetch i.targetUser t" +
            " left join fetch i.sender s" +
            " left join fetch i.channel c" +
            " where t.id = :userId and i.expirationDate > :expire" +
            " and i.status = :status")
    List<ChatChannelInvite> findInvitesByTargetAndStatusWithSenderAndTargetAndChannel(@Param("userId") long userId, @Param("expire") Timestamp expireAfter, @Param("status") ChannelInviteStatus status);
}
