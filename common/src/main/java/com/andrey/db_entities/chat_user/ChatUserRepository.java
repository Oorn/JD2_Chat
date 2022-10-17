package com.andrey.db_entities.chat_user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatUserRepository extends CrudRepository<ChatUser, Long>
        , JpaRepository<ChatUser, Long>
        , PagingAndSortingRepository<ChatUser, Long> {

    ChatUser findChatUserById(Long id);
}
