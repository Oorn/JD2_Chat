package com.andrey.db_entities.chat_friendship;

import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatFriendshipRepository extends CrudRepository<ChatFriendship, Long>
        , JpaRepository<ChatFriendship, Long>
        , PagingAndSortingRepository<ChatFriendship, Long> {

    ChatFriendship findChatFriendshipById(Long id);
}
