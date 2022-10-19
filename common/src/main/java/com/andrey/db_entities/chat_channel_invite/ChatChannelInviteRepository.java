package com.andrey.db_entities.chat_channel_invite;

import com.andrey.db_entities.chat_channel.ChatChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatChannelInviteRepository extends CrudRepository<ChatChannelInvite, Long>
        , JpaRepository<ChatChannelInvite, Long>
        , PagingAndSortingRepository<ChatChannelInvite, Long> {

    ChatChannelInvite findChatChannelInviteById(Long id);
}
