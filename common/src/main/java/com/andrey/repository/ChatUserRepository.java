package com.andrey.repository;

import com.andrey.db_entities.chat_user.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatUserRepository extends CrudRepository<ChatUser, Long>
        , JpaRepository<ChatUser, Long>
        , PagingAndSortingRepository<ChatUser, Long> {

    Optional<ChatUser> findChatUserById(Long id);
    ChatUser findChatUserByEmail(String email);
    Boolean existsByEmail(String email);

    @Query(value = "select u from ChatUser u" +
            " left join fetch u.channelMemberships m" +
            " left join fetch m.channel mc" +
            " left join fetch u.friendshipsWithGreaterID f1" +
            " left join fetch f1.userWithLesserID f1u" +
            " left join fetch u.friendshipsWithLesserID f2" +
            " left join fetch f2.userWithGreaterID f2u" +
            " left join fetch u.blocks b" +
            " left join fetch b.blockedUser bu" +
            " where u.email = :email ")
    ChatUser findChatUserByEmailWithFriendshipsAndChatMembershipsAndBlocks(@Param("email") String email);

    @Query(value = "select u from ChatUser u" +
            " left join fetch u.channelMemberships m" +
            " left join fetch m.channel mc" +
            " left join fetch m.userProfile p" +
            " where u.id = :id ")
    Optional<ChatUser> findChatUserByIdWithChatMembershipsWithChatProfileAndChannel (@Param("id") Long id);

    @Query(value = "select u from ChatUser u" +
            " left join fetch u.ownedProfiles p" +
            " where u.id = :id ")
    Optional<ChatUser> findChatUserByIdWithProfiles (@Param("id") Long id);

    @Query(value = "select u from ChatUser u" +
            " left join fetch u.blocks b" +
            " where u.id = :id ")
    Optional<ChatUser> findChatUserByIdWithBlocks (@Param("id") Long id);
}
