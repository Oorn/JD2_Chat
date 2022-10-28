package com.andrey.repository;

import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_profile.ChatProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatChannelRepository extends CrudRepository<ChatChannel, Long>
        , JpaRepository<ChatChannel, Long>
        , PagingAndSortingRepository<ChatChannel, Long> {

    ChatChannel findChatChannelById(Long id);

    @Query(value = "select c from ChatChannel c" +
            " left join fetch c.members mem" +
            " left join fetch mem.userProfile p" +
            " left join fetch mem.user u" +
            " where c.id = :id ")
    Optional<ChatChannel> findChatChannelByIdWithMembershipsWithProfileAndUser(Long id);
}
