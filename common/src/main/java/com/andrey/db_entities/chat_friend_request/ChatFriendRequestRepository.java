package com.andrey.db_entities.chat_friend_request;

import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatFriendRequestRepository extends CrudRepository<ChatFriendRequest, Long>
        , JpaRepository<ChatFriendRequest, Long>
        , PagingAndSortingRepository<ChatFriendRequest, Long> {

    ChatFriendRequest findChatFriendRequestById(Long id);
}
