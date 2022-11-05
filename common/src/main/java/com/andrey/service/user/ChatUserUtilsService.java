package com.andrey.service.user;

import com.andrey.db_entities.chat_channel_membership.ChannelMembershipRole;
import com.andrey.db_entities.chat_user.ChatUser;

public interface ChatUserUtilsService {

    //doesn't work on AuthUser
    long getActiveProfileNumber (ChatUser user);

    boolean checkFriendship(ChatUser userWithLoadedFriendships, ChatUser secondUser);
    //by convention, user is friend to himself

    boolean checkIfAuthUserCanInviteToChannel(ChatUser authUser, long channelId);

    boolean checkIfAuthUserCanPostInChannel(ChatUser authUser, long channelId);

    boolean checkIfAuthUserCanReadInChannel(ChatUser authUser, long channelId);

    boolean checkIfAuthUserCanModerateChannel(ChatUser authUser, long channelId);

    boolean checkIfAuthUserCanChangeOthersRoleInChannel(ChatUser authUser, long channelId, ChannelMembershipRole oldRole, ChannelMembershipRole newRole);

    boolean checkIfAuthUserChannelMember(ChatUser authUser, long channelId);

    boolean checkIfBlockIsPresent(ChatUser authUser, long targetUserId);

    long getOwnedMultiuserChannelNumber (ChatUser authUser);

}
