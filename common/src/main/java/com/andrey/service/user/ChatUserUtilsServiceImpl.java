package com.andrey.service.user;

import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.db_entities.chat_user.ChatUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatUserUtilsServiceImpl implements ChatUserUtilsService{
    @Override
    public long getActiveProfileNumber(ChatUser user) {
        return user.getOwnedProfiles().stream()
                .filter(ChatProfile::isInteractable)
                .count();
    }

    @Override
    public boolean checkFriendship(ChatUser userWithLoadedFriendships, ChatUser secondUser) {
        if (userWithLoadedFriendships.getId() > secondUser.getId())
            return userWithLoadedFriendships
                    .getFriendshipsWithGreaterID()
                    .containsKey(secondUser.getId());

        if (userWithLoadedFriendships.getId() < secondUser.getId())
            return userWithLoadedFriendships
                    .getFriendshipsWithLesserID()
                    .containsKey(secondUser.getId());

        return true;
    }
}
