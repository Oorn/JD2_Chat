package com.andrey.db_entities.chat_friendship;

import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import com.andrey.db_entities.chat_user.ChatUser;
import java.util.Collections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatFriendshipRepository extends CrudRepository<ChatFriendship, Long>
        , JpaRepository<ChatFriendship, Long>
        , PagingAndSortingRepository<ChatFriendship, Long> {

    ChatFriendship findChatFriendshipById(Long id);
    List<ChatFriendship> findChatFriendshipsByUserWithGreaterID(ChatUser user);
    List<ChatFriendship> findChatFriendshipsByUserWithLesserID(ChatUser user);

    boolean existsByUserWithGreaterIDAndUserWithLesserIDAndStatus(ChatUser user1, ChatUser user2, FriendshipStatus status);

    List<ChatFriendship> findChatFriendshipsByUserWithGreaterIDAndUserWithLesserID(ChatUser user1, ChatUser user2);

    default List<ChatFriendship> findChatFriendshipsByUser(ChatUser user) {
        List<ChatFriendship> result = findChatFriendshipsByUserWithGreaterID(user);
        result.addAll(findChatFriendshipsByUserWithLesserID(user));
        return result;
    }

    default boolean checkFriendshipStatus(ChatUser user1, ChatUser user2) {

        if (user2.getId() > user1.getId())
        {
            ChatUser t = user1;
            user1 = user2;
            user2 = t;
        }
        return existsByUserWithGreaterIDAndUserWithLesserIDAndStatus(user1, user2, FriendshipStatus.ACTIVE);
    }

    default List<ChatFriendship> findFriendship(ChatUser user1, ChatUser user2) {

        if (user2.getId() > user1.getId())
        {
            ChatUser t = user1;
            user1 = user2;
            user2 = t;
        }
        return findChatFriendshipsByUserWithGreaterIDAndUserWithLesserID(user1, user2);
    }

    default ChatFriendship createFriendship(ChatUser user1, ChatUser user2) {
        if (user2.getId() > user1.getId())
        {
            ChatUser t = user1;
            user1 = user2;
            user2 = t;
        }
        List<ChatFriendship> oldFriendships = findChatFriendshipsByUserWithGreaterIDAndUserWithLesserID(user1, user2);
        if (oldFriendships.isEmpty())
        {
            //create new
            return saveAndFlush(ChatFriendship.builder()
                    .userWithGreaterID(user1)
                    .userWithLesserID(user2)
                    .status(FriendshipStatus.ACTIVE)
                    .build());
        }
        ChatFriendship oldFriendship = oldFriendships.get(0);
        if (oldFriendship.getStatus() == FriendshipStatus.REMOVED) {
            //change existing
            oldFriendship.setStatus(FriendshipStatus.ACTIVE);
            return saveAndFlush(oldFriendship);
        }
        //nothing needs changing
        return oldFriendship;
    }
    default ChatFriendship removeFriendshipIfExists(ChatUser user1, ChatUser user2) {
        if (user2.getId() > user1.getId())
        {
            ChatUser t = user1;
            user1 = user2;
            user2 = t;
        }
        List<ChatFriendship> oldFriendships = findChatFriendshipsByUserWithGreaterIDAndUserWithLesserID(user1, user2);
        if (oldFriendships.isEmpty())
        {
            //nothing to change
            return null;
        }
        ChatFriendship oldFriendship = oldFriendships.get(0);
        if (oldFriendship.getStatus() == FriendshipStatus.REMOVED) {
            //nothing to change
            return oldFriendship;
        }
        //ser status to REMOVED
        oldFriendship.setStatus(FriendshipStatus.REMOVED);
        return saveAndFlush(oldFriendship);
    }
}
