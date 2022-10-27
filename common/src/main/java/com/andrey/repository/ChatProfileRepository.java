package com.andrey.repository;

import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.db_entities.chat_user.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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


    @Query (nativeQuery = true, value = "select p.* from chat.profiles as p" +
            " inner join chat.users as u on p.owner_user_id = u.id" +
            " left join chat.block_list as b on b.blocked_user_id = :userid" +
                " and b.blocking_user_id = u.id and (b.status = 'ACTIVE' or b.status = 'MIRRORED')" +
            " where p.status = 'OK' and u.status = 'OK' and p.visibility_matchmaking = 'VISIBLE'" +
                " and u.id != :userid and b.id is null" +
            " order by random() limit :amount")
    //this is pretty bad approach, as it first does full select, then full sort on random
    //'fast' random solution is done by estimating size of select beforehand,
    // and filtering through an estimate percentage with TABLESAMPLE
    @Deprecated
    List<ChatProfile> getRandomMatchmakingProfilesForUser
            (@Param("amount") Integer amount,
             @Param("userid") Long userId);


    @Query (nativeQuery = true, value = "select p.* from chat.profiles as p" +
            " tablesample chat.system_rows(:sample)" +
            " inner join chat.users as u on p.owner_user_id = u.id" +
            " left join chat.block_list as b on b.blocked_user_id = :userid" +
                " and b.blocking_user_id = u.id and (b.status = 'ACTIVE' or b.status = 'MIRRORED')" +
            " where p.status = 'OK' and u.status = 'OK' and p.visibility_matchmaking = 'VISIBLE'" +
                " and u.id != :userid and b.id is null" +
            " order by random() limit :amount")
    List<ChatProfile> getRandomMatchmakingProfilesForUserWithSample
            (@Param("amount") int amount,
             @Param("userid") long userId,
             @Param("sample") int sampleSize);
}
