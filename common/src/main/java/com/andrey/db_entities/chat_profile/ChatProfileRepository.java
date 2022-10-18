package com.andrey.db_entities.chat_profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatProfileRepository extends CrudRepository<ChatProfile, Long>
        , JpaRepository<ChatProfile, Long>
        , PagingAndSortingRepository<ChatProfile, Long> {

    ChatProfile findChatProfileById(Long id);
}
