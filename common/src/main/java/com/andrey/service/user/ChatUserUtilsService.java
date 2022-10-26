package com.andrey.service.user;

import com.andrey.db_entities.chat_user.ChatUser;

public interface ChatUserUtilsService {
    long getActiveProfileNumber (ChatUser user);

    boolean checkFriendship(ChatUser userWithLoadedFriendships, ChatUser secondUser);
    //by convention, user is friend to himself
}
