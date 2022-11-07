package com.andrey.service.user;

import com.andrey.db_entities.chat_block.BlockStatus;
import com.andrey.db_entities.chat_channel.ChannelType;
import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_channel_membership.ChannelMembershipRole;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.service.channel.ChannelNamingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatUserUtilsServiceImpl implements ChatUserUtilsService{

    private final ChannelNamingService channelNamingService;

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
    public boolean checkIfAuthUserCanInviteToChannel(ChatUser authUser, long channelId) {
        if (!checkIfAuthUserChannelMember(authUser, channelId))
            return false;
        ChatChannelMembership membership = authUser.getChannelMemberships().get(channelId);
        switch (membership.getRole()) {
            case OWNER:
            case ADMIN:
            case MODERATOR:
            case READ_WRITE_INVITE_ACCESS:
                return true;
            default:
                return false;
        }
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
            userIds = channelNamingService.getMemberIdsFromChannelName(channel.getChannelName());
        else
            userIds = channelNamingService.getMemberIdsFromChannelName(channel.getChannelName());
        long user1 = userIds[0];
        long user2 = userIds[1];
        if (authUser.getId().equals(user1))
            return checkIfBlockIsPresent(authUser, user2);
        else
            return checkIfBlockIsPresent(authUser, user1);
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
    public boolean checkIfAuthUserCanChangeOthersRoleInChannel(ChatUser authUser, long channelId, ChannelMembershipRole oldRole, ChannelMembershipRole newRole) {
        if (!checkIfAuthUserChannelMember(authUser, channelId))
            return false;
        ChannelMembershipRole authRole = authUser.getChannelMemberships().get(channelId).getRole();

        if (multiUserRoleAccessLevelInfo(authRole) < 2)
            return false; //needs at least MODERATOR
        if (multiUserRoleAccessLevelInfo(oldRole) == -1)
            return false; //invalid role
        if (multiUserRoleAccessLevelInfo(newRole) == -1)
            return false; //invalid role

        if (multiUserRoleAccessLevelInfo(authRole) <= multiUserRoleAccessLevelInfo(oldRole))
            return false; //old role too high to take
        if (multiUserRoleAccessLevelInfo(authRole) <= multiUserRoleAccessLevelInfo(newRole))
            return false; //new role too high to give

        return true;

    }

    private int multiUserRoleAccessLevelInfo(ChannelMembershipRole role) {
        switch (role) {
            case OWNER:
                return 4;
            case ADMIN:
                return 3;
            case MODERATOR:
                return 2;
            case NO_ACCESS:
            case READ_ACCESS:
            case READ_WRITE_ACCESS:
            case READ_WRITE_INVITE_ACCESS:
                return 1;
            default:
                return -1; //INVALID
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
    public boolean checkIfBlockIsPresent(ChatUser authUser, long targetUserId) {
        if (!authUser.getBlocks().containsKey(targetUserId))
            return false;
        if (authUser.getBlocks().get(targetUserId).getStatus().equals(BlockStatus.REMOVED))
            return false;
        return true;
    }

    @Override
    public long getOwnedMultiuserChannelNumber(ChatUser authUser) {
        return authUser.getChannelMemberships().values().stream()
                .filter(m -> m.getRole().equals(ChannelMembershipRole.OWNER))
                .filter(ChatChannelMembership::isInteractable)
                .map(ChatChannelMembership::getChannel)
                .filter(ChatChannel::isInteractable)
                .count();


    }


}
