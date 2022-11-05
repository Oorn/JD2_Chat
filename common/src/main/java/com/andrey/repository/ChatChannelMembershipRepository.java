package com.andrey.repository;

import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatChannelMembershipRepository extends CrudRepository<ChatChannelMembership, Long>
        , JpaRepository<ChatChannelMembership, Long>
        , PagingAndSortingRepository<ChatChannelMembership, Long> {

    ChatChannelMembership findChatChannelMembershipById(Long id);

    @Query(value = "select m from ChatChannelMembership m" +
            " left join fetch m.user u" +
            " left join fetch m.channel c" +
            " where u.id = :userId and c.id = :channelId")
    Optional<ChatChannelMembership> findChatChannelMembershipByUserIdChannelIdWithUsers (@Param("channelId") long channelId, @Param("userId") long userId);
}
