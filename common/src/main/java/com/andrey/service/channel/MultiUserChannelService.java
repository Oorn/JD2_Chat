package com.andrey.service.channel;

import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_channel_invite.ChatChannelInvite;
import com.andrey.db_entities.chat_channel_membership.ChannelMembershipRole;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import com.andrey.db_entities.chat_user.ChatUser;

import java.util.List;
import java.util.Optional;

public interface MultiUserChannelService {

    Optional<ChatChannel> createMultiuserChannel(ChatUser authUser, ChatChannel newChannel);
    Optional<ChatChannel> updateMultiuserChannel(ChatUser authUser, ChatChannel newChannel);
    Optional<ChatChannel> deleteMultiuserChannel(ChatUser authUser, Long channelId);
    Optional<ChatChannelMembership> updateUserRole(ChatUser authUser, Long channelId, Long userId, ChannelMembershipRole newRole);

    Optional<ChatChannelInvite> sendChannelInviteToUser(ChatUser authUser, Long channelId, Long targetUserId);

}
