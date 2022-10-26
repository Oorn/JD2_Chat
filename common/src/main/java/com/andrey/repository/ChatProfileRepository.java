package com.andrey.repository;

import com.andrey.db_entities.chat_profile.ChatProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatProfileRepository extends CrudRepository<ChatProfile, Long>
        , JpaRepository<ChatProfile, Long>
        , PagingAndSortingRepository<ChatProfile, Long> {

    Optional<ChatProfile> findChatProfileById(Long id);

    @Query(value = "select p from ChatProfile p" +
            " left join fetch p.owner u" +
            " where p.id = :id ")
    Optional<ChatProfile> findChatProfileByIdWithOwner(@Param("id") Long id);


}
