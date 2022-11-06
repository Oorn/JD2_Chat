package com.andrey.service.channel_invite;

import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_channel_invite.ChannelInviteStatus;
import com.andrey.db_entities.chat_channel_invite.ChannelInviteType;
import com.andrey.db_entities.chat_channel_invite.ChatChannelInvite;
import com.andrey.db_entities.chat_channel_membership.ChannelMembershipStatus;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.exceptions.BadTargetException;
import com.andrey.exceptions.NoPermissionException;
import com.andrey.exceptions.NoSuchEntityException;
import com.andrey.exceptions.RemovedEntityException;
import com.andrey.repository.ChatChannelInviteRepository;
import com.andrey.repository.ChatChannelMembershipRepository;
import com.andrey.service.cached_user_details.CachedUserDetailsService;
import com.andrey.service.membership.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChannelInviteServiceImpl implements  ChannelInviteService{
    private final ChatChannelInviteRepository inviteRepository;

    private final ChatChannelMembershipRepository membershipRepository;
    private final MembershipService membershipService;

    private final CachedUserDetailsService cacheUserService;

    @Override
    public Optional<ChatChannelMembership> acceptPersonalChannelInvite(ChatUser authUser, Long inviteId) {
        Optional<ChatChannelInvite> optionalInvite = inviteRepository.findChatChannelInviteByIdWithSenderAndTargetAndChannel(inviteId);
        if (optionalInvite.isEmpty())
            throw new NoSuchEntityException("invite " + inviteId + " does not exist");
        ChatChannelInvite invite = optionalInvite.get();

        if (!invite.getInviteType().equals(ChannelInviteType.SINGLE_USER_INVITE))
            throw new BadTargetException("invite " + inviteId + " is not personal invite");
        if (!invite.getTargetUser().getId().equals(authUser.getId()))
            throw new NoPermissionException("invite " + inviteId + " is not intended for user " + authUser.getId());
        if (!invite.getStatus().equals(ChannelInviteStatus.ACTIVE))
            throw new RemovedEntityException("invite " + inviteId + " is not valid");
        Timestamp now = new Timestamp(new Date().getTime());
        if (invite.getExpirationDate().before(now))
            throw new RemovedEntityException("invite " + inviteId + " has expired");
        if (!invite.getChannel().isInteractable())
            throw new RemovedEntityException("channel " + invite.getChannel().getId() + " has been removed");

        incrementInviteAndSave(invite);
        if (authUser.getChannelMemberships().containsKey(invite.getChannel().getId())) {
            ChatChannelMembership oldMembership = authUser.getChannelMemberships().get(invite.getChannel().getId());
            if (oldMembership.isInteractable())
                return Optional.of(oldMembership);
            oldMembership.setStatus(ChannelMembershipStatus.ACTIVE);
            oldMembership.setRole(invite.getChannel().getDefaultRole());
            return Optional.of(membershipRepository.saveAndFlush(oldMembership));

        }
        Optional<ChatChannelMembership> result = Optional.ofNullable(membershipService.createNewMembershipAndSaveNoCheck(authUser, invite.getChannel(), Optional.empty(), invite.getChannel().getDefaultRole()));
        cacheUserService.evictUserFromCache(authUser);
        return result;

    }

    @Override
    public Optional<ChatChannelInvite> declinePersonalChannelInvite(ChatUser authUser, Long inviteId) {
        Optional<ChatChannelInvite> optionalInvite = inviteRepository.findChatChannelInviteByIdWithSenderAndTargetAndChannel(inviteId);
        if (optionalInvite.isEmpty())
            throw new NoSuchEntityException("invite " + inviteId + " does not exist");
        ChatChannelInvite invite = optionalInvite.get();

        if (!invite.getInviteType().equals(ChannelInviteType.SINGLE_USER_INVITE))
            throw new BadTargetException("invite " + inviteId + " is not personal invite");
        if (!invite.getTargetUser().getId().equals(authUser.getId()))
            throw new NoPermissionException("invite " + inviteId + " is not intended for user " + authUser.getId());
        if (!invite.getStatus().equals(ChannelInviteStatus.ACTIVE))
            throw new RemovedEntityException("invite " + inviteId + " is not valid");
        Timestamp now = new Timestamp(new Date().getTime());
        if (invite.getExpirationDate().before(now))
            throw new RemovedEntityException("invite " + inviteId + " has expired");

        incrementInviteAndSave(invite);
        return Optional.of(invite);
    }

    private void incrementInviteAndSave(ChatChannelInvite invite) {
        invite.setTimesUsed(invite.getTimesUsed() + 1);
        if (invite.getTimesUsed() >= invite.getMaxUses())
            invite.setStatus(ChannelInviteStatus.MAX_USES_REACHED);
        inviteRepository.saveAndFlush(invite);
    }

    @Override
    public List<ChatChannelInvite> getPendingChannelInvites(ChatUser authUser) {
        return inviteRepository.findInvitesByTargetAndStatusWithSenderAndTargetAndChannel(authUser.getId(), new Timestamp(new Date().getTime()), ChannelInviteStatus.ACTIVE);
    }
}
