package com.andrey.service.channel;

import com.andrey.Constants;
import com.andrey.db_entities.chat_channel.ChannelStatus;
import com.andrey.db_entities.chat_channel.ChannelType;
import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_channel_invite.ChannelInviteStatus;
import com.andrey.db_entities.chat_channel_invite.ChannelInviteType;
import com.andrey.db_entities.chat_channel_invite.ChatChannelInvite;
import com.andrey.db_entities.chat_channel_membership.ChannelMembershipRole;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.exceptions.BadTargetException;
import com.andrey.exceptions.InteractionWithSelfException;
import com.andrey.exceptions.NoPermissionException;
import com.andrey.exceptions.NoSuchEntityException;
import com.andrey.exceptions.RemovedEntityException;
import com.andrey.exceptions.TooManyEntitiesException;
import com.andrey.repository.ChatChannelInviteRepository;
import com.andrey.repository.ChatChannelMembershipRepository;
import com.andrey.repository.ChatChannelRepository;
import com.andrey.repository.ChatUserRepository;
import com.andrey.service.MessageUpdateDatePropagateService;
import com.andrey.service.cached_user_details.CachedUserDetailsService;
import com.andrey.service.membership.MembershipService;
import com.andrey.service.user.ChatUserUtilsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MultiUserChannelServiceImpl implements MultiUserChannelService{

    private final ChatChannelRepository channelRepository;
    private final ChatChannelMembershipRepository membershipRepository;

    private final ChatUserRepository userRepository;

    private final ChatChannelInviteRepository inviteRepository;

    private final MembershipService membershipService;

    private final ChatUserUtilsService userUtils;

    private final MessageUpdateDatePropagateService propagateService;

    private final CachedUserDetailsService cacheUserService;

    @Override
    public Optional<ChatChannel> createMultiuserChannel(ChatUser authUser, ChatChannel newChannel) {

        if (userUtils.getOwnedMultiuserChannelNumber(authUser) >= Constants.MAX_OWNED_MULTIUSER_CHANNELS)
            throw new TooManyEntitiesException("exceeding maximum allowed number if multiuser channels per user of " + Constants.MAX_OWNED_MULTIUSER_CHANNELS);

        newChannel.setOwner(authUser);
        channelRepository.save(newChannel);
        membershipService.createNewMembershipAndSaveNoCheck(authUser, newChannel, Optional.empty(), ChannelMembershipRole.OWNER);

        cacheUserService.evictUserFromCache(authUser);
        return Optional.of(newChannel);

    }

    @Override
    public Optional<ChatChannel> updateMultiuserChannel(ChatUser authUser, ChatChannel newChannel) {
        Optional<ChatChannel> optionalOldChannel = channelRepository.findChatChannelById(newChannel.getId());
        if (optionalOldChannel.isEmpty())
            throw new NoSuchEntityException("channel " + newChannel.getId() + " does not exist");
        ChatChannel oldChannel = optionalOldChannel.get();
        if (!oldChannel.getOwner().getId().equals(authUser.getId()))
            throw new NoPermissionException("user " + authUser.getId() + " does not own channel " + newChannel.getId());
        if (!oldChannel.isInteractable())
            throw new RemovedEntityException("channel " + oldChannel.getId() + " has been removed");

        oldChannel.setChannelName(newChannel.getChannelName());
        oldChannel.setDefaultRole(newChannel.getDefaultRole());


        channelRepository.saveAndFlush(oldChannel);
        propagateService.updateDateAndPropagate(oldChannel);

        return Optional.of(oldChannel);
    }

    @Override
    public Optional<ChatChannel> deleteMultiuserChannel(ChatUser authUser, Long channelId) {
        Optional<ChatChannel> optionalOldChannel = channelRepository.findChatChannelByIdWithMembershipsWithProfileAndUser(channelId);

        if (optionalOldChannel.isEmpty())
            throw new NoSuchEntityException("channel " + channelId + " does not exist");
        ChatChannel oldChannel = optionalOldChannel.get();
        if (!oldChannel.getChannelType().equals(ChannelType.MULTI_USER_CHANNEL))
            throw new BadTargetException("channel " + channelId + " isn't a multiuser channel");
        if (!oldChannel.getOwner().getId().equals(authUser.getId()))
            throw new NoPermissionException("user " + authUser.getId() + " doesn't own channel " + channelId);
        if (!oldChannel.isInteractable())
            throw new RemovedEntityException("channel " + channelId + " has been removed");

        oldChannel.setStatus(ChannelStatus.REMOVED);

        channelRepository.saveAndFlush(oldChannel);
        propagateService.updateDateAndPropagate(oldChannel);

        return Optional.of(oldChannel);
    }


    @Override
    public Optional<ChatChannelMembership> updateUserRole(ChatUser authUser, Long channelId, Long userId, ChannelMembershipRole newRole) {

        if (authUser.getId().equals(userId))
            throw new InteractionWithSelfException("user cannot change their own role");

        if (!authUser.getChannelMemberships().containsKey(channelId))
            throw new NoSuchEntityException("user " + authUser.getId() + " is not member of channel " + channelId);

        ChatChannelMembership authMembership = authUser.getChannelMemberships().get(channelId);

        if (!authMembership.isInteractable())
            throw new NoSuchEntityException("user " + authUser.getId() + " is not member of channel " + channelId);

        if (!authMembership.getChannel().getChannelType().equals(ChannelType.MULTI_USER_CHANNEL))
            throw new BadTargetException("channel " + channelId + " isn't a multiuser channel");

        if (!authMembership.getChannel().isInteractable())
            throw new RemovedEntityException("channel " + channelId + " has been removed");

        if (!userUtils.checkIfAuthUserCanModerateChannel(authUser, channelId))
            throw new NoPermissionException("user " + authUser.getId() + " cannot moderate channel " + channelId);

        Optional<ChatChannelMembership> optionalMembership = membershipRepository.findChatChannelMembershipByUserIdChannelIdWithUsers(channelId, userId);
        if (optionalMembership.isEmpty())
            throw new NoSuchEntityException("user " + userId + " is not member of channel " + channelId);
        ChatChannelMembership membership = optionalMembership.get();

        if (!membership.isInteractable())
            throw new NoSuchEntityException("user " + userId + " is not member of channel " + channelId);

        if (!membership.getUser().isInteractable())
            throw new RemovedEntityException("user " + userId + " has been deleted");

        if (!userUtils.checkIfAuthUserCanChangeOthersRoleInChannel(authUser, channelId, membership.getRole(), newRole))
            throw new NoPermissionException("user " + authUser.getId() + " does not have sufficiently high role");

        membership.setRole(newRole);
        membershipRepository.saveAndFlush(membership);
        cacheUserService.evictUserFromCache(membership.getUser());
        return Optional.of(membership);
    }

    @Override
    public Optional<ChatChannelInvite> sendChannelInviteToUser(ChatUser authUser, Long channelId, Long targetUserId) {
        if (!userUtils.checkIfAuthUserCanInviteToChannel(authUser,channelId))
            throw new NoPermissionException("user " + authUser.getId() + " cannot send invites to channel " + channelId);
        if (userUtils.checkIfBlockIsPresent(authUser,targetUserId))
            throw new NoPermissionException("user " + authUser.getId() + " is blocked by user " + targetUserId);
        Optional<ChatUser> optionalInviteUser = userRepository.findChatUserById(targetUserId);
        if (optionalInviteUser.isEmpty())
            throw new NoSuchEntityException("user " + targetUserId + " does not exist");
        ChatUser inviteUser = optionalInviteUser.get();
        if (!inviteUser.isInteractable())
            throw new RemovedEntityException("user " + targetUserId + " has been removed");

        ChatChannelInvite invite = ChatChannelInvite.builder()
                .sender(authUser)
                .channel(authUser.getChannelMemberships().get(channelId).getChannel())
                .inviteType(ChannelInviteType.SINGLE_USER_INVITE)
                .targetUser(inviteUser)
                .maxUses(1)
                .timesUsed(0)
                .status(ChannelInviteStatus.ACTIVE)
                .expirationDate(new Timestamp(new Date().getTime() + Constants.PRIVATE_INVITE_TO_CHANNEL_EXPIRE_MILLIS))
                .build();

        inviteRepository.saveAndFlush(invite);

        return Optional.of(invite);
    }



}
