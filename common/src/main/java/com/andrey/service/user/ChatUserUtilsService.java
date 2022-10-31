package com.andrey.service.user;

import com.andrey.db_entities.chat_user.ChatUser;

public interface ChatUserUtilsService {
    long getActiveProfileNumber (ChatUser user);

    boolean checkFriendship(ChatUser userWithLoadedFriendships, ChatUser secondUser);
    //by convention, user is friend to himself

    boolean checkIfAuthUserCanPostInChannel(ChatUser authUser, long channelId);

    boolean checkIfAuthUserCanReadInChannel(ChatUser authUser, long channelId);

    boolean checkIfAuthUserCanModerateChannel(ChatUser authUser, long channelId);

    boolean checkIfAuthUserChannelMember(ChatUser authUser, long channelId);

    boolean checkIfBLockIsPresent(ChatUser authUser, long targetUserId);

}
