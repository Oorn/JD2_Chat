package com.andrey.db_entities.chat_channel_membership;

import com.andrey.db_entities.chat_channel.ChatChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatChannelMembershipRepository extends CrudRepository<ChatChannelMembership, Long>
        , JpaRepository<ChatChannelMembership, Long>
        , PagingAndSortingRepository<ChatChannelMembership, Long> {

    ChatChannelMembership findChatChannelMembershipById(Long id);
}
