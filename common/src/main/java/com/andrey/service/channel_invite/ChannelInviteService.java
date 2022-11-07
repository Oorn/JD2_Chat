package com.andrey.service.channel_invite;

import com.andrey.db_entities.chat_channel_invite.ChatChannelInvite;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import com.andrey.db_entities.chat_user.ChatUser;

import java.util.List;
import java.util.Optional;

public interface ChannelInviteService {

    Optional<ChatChannelMembership> acceptPersonalChannelInvite(ChatUser authUser, Long inviteId);

    Optional<ChatChannelInvite> declinePersonalChannelInvite(ChatUser authUser, Long inviteId);

    List<ChatChannelInvite> getPendingChannelInvites(ChatUser authUser);

}
