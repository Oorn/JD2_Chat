package com.andrey.db_entities.chat_user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatUserRepository extends CrudRepository<ChatUser, Long>
        , JpaRepository<ChatUser, Long>
        , PagingAndSortingRepository<ChatUser, Long> {

    ChatUser findChatUserById(Long id);
    ChatUser findChatUserByEmail(String email);
    Boolean existsByEmail(String email);

    /*@Query(value = "select u from ChatUser u" +
            " join fetch u.channelMemberships m" +
            " join fetch m.channel mc" +
            " join fetch u.friendshipsWithGreaterID f1" +
            " join fetch f1.userWithLesserID f1u" +
            " join fetch u.friendshipsWithLesserID f2" +
            " join fetch f2.userWithGreaterID f2u" +
            " where u.email = :email ")*/
    @Query(value = "select u from ChatUser u" +
            " where u.email = :email ")
    ChatUser findChatUserByEmailWithFriendshipsAndChatMemberships (@Param("email") String email);
}
