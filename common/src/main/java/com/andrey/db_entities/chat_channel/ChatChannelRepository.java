package com.andrey.db_entities.chat_channel;

import com.andrey.db_entities.chat_profile.ChatProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatChannelRepository extends CrudRepository<ChatChannel, Long>
        , JpaRepository<ChatChannel, Long>
        , PagingAndSortingRepository<ChatChannel, Long> {

    ChatChannel findChatChannelById(Long id);
}
