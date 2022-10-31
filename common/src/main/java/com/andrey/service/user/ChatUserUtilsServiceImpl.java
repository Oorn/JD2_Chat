package com.andrey.service.user;

import com.andrey.db_entities.chat_block.BlockStatus;
import com.andrey.db_entities.chat_channel.ChannelStatus;
import com.andrey.db_entities.chat_channel.ChannelType;
import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_channel_membership.ChannelMembershipStatus;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.service.channel.PrivateChannelService;
import com.andrey.service.channel.PrivateChannelServiceImpl;
import com.andrey.service.channel.ProfileChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatUserUtilsServiceImpl implements ChatUserUtilsService{

    private final PrivateChannelService privateChannelService;
    private final ProfileChannelService profileChannelService;

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

    @Override
    public boolean checkIfAuthUserCanPostInChannel(ChatUser authUser, long channelId) {
        if (!checkIfAuthUserChannelMember(authUser, channelId))
            return false;
        ChatChannelMembership membership = authUser.getChannelMemberships().get(channelId);
        switch (membership.getRole()) {
            case OWNER:
            case ADMIN:
            case MODERATOR:
            case READ_WRITE_INVITE_ACCESS:
            case READ_WRITE_ACCESS:
                return true;
            case PRIVATE_CHANNEL_ACCESS:
            case PROFILE_CHANNEL_ACCESS:
                return !privateChannelBlockCheck(authUser, membership.getChannel());
            default:
                return false;
        }
    }

    //assuming membership and channel aren't REMOVED
    private boolean privateChannelBlockCheck(ChatUser authUser, ChatChannel channel) {
        long[] userIds;
        if (channel.getChannelType().equals(ChannelType.PRIVATE_CHAT_FROM_PROFILE))
            userIds = profileChannelService.getMemberIdsFromChannelName(channel.getChannelName());
        else
            userIds = privateChannelService.getMemberIdsFromChannelName(channel.getChannelName());
        long user1 = userIds[0];
        long user2 = userIds[1];
        if (authUser.getId().equals(user1))
            return checkIfBLockIsPresent(authUser, user2);
        else
            return checkIfBLockIsPresent(authUser, user1);
    }

    @Override
    public boolean checkIfAuthUserCanReadInChannel(ChatUser authUser, long channelId) {
        if (!checkIfAuthUserChannelMember(authUser, channelId))
            return false;

        ChatChannelMembership membership = authUser.getChannelMemberships().get(channelId);
        switch (membership.getRole()) {
            case OWNER:
            case ADMIN:
            case MODERATOR:
            case READ_WRITE_INVITE_ACCESS:
            case READ_WRITE_ACCESS:
            case READ_ACCESS:
            case PRIVATE_CHANNEL_ACCESS:
            case PROFILE_CHANNEL_ACCESS:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean checkIfAuthUserCanModerateChannel(ChatUser authUser, long channelId) {
        if (!checkIfAuthUserChannelMember(authUser, channelId))
            return false;

        ChatChannelMembership membership = authUser.getChannelMemberships().get(channelId);
        switch (membership.getRole()) {
            case OWNER:
            case ADMIN:
            case MODERATOR:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean checkIfAuthUserChannelMember(ChatUser authUser, long channelId) {

        if (!authUser.getChannelMemberships().containsKey(channelId))
            return false;
        ChatChannelMembership membership = authUser.getChannelMemberships().get(channelId);
        if (!membership.isInteractable())
            return false;
        if (!membership.getChannel().isInteractable())
            return false;

        return true;
    }

    @Override
    public boolean checkIfBLockIsPresent(ChatUser authUser, long targetUserId) {
        if (!authUser.getBlocks().containsKey(targetUserId))
            return false;
        if (authUser.getBlocks().get(targetUserId).getStatus().equals(BlockStatus.REMOVED))
            return false;
        return true;
    }


}
