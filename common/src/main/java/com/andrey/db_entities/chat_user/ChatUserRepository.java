package com.andrey.db_entities.chat_user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatUserRepository extends CrudRepository<ChatUser, Long>
        , JpaRepository<ChatUser, Long>
        , PagingAndSortingRepository<ChatUser, Long> {

    ChatUser findChatUserById(Long id);
    ChatUser findChatUserByEmail(String email);
    Boolean existsByEmail(String email);

    @Query(value = "select u from ChatUser u" +
            " inner join fetch u.channelMemberships m" +
            " inner join fetch m.channel mc" +
            " inner join fetch u.friendshipsWithGreaterID f1" +
            " inner join fetch f1.userWithLesserID f1u" +
            " inner join fetch u.friendshipsWithLesserID f2" +
            " inner join fetch f2.userWithGreaterID f2u")
    ChatUser findChatUserByEmailWithFriendshipsAndChatMemberships (String email);
}
