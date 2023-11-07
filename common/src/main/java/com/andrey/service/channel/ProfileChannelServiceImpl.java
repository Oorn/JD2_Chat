package com.andrey.service.channel;

import com.andrey.db_entities.chat_channel.ChannelStatus;
import com.andrey.db_entities.chat_channel.ChannelType;
import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_channel_membership.ChannelMembershipRole;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.db_entities.chat_profile.ProfileVisibilityMatchmaking;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.exceptions.BadTargetException;
import com.andrey.exceptions.InteractionWithSelfException;
import com.andrey.exceptions.NoPermissionException;
import com.andrey.exceptions.NoSuchEntityException;
import com.andrey.exceptions.RemovedEntityException;
import com.andrey.repository.ChatChannelRepository;
import com.andrey.repository.ChatProfileRepository;
import com.andrey.service.membership.MembershipService;
import com.andrey.service.user.ChatUserUtilsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileChannelServiceImpl implements ProfileChannelService {
    private final ChatProfileRepository profilesRepository;

    private final ChatChannelRepository channelRepository;

    private final MembershipService membershipService;

    private final EntityManager entityManager;

    private final ChatUserUtilsService userUtils;

    private final ChannelNamingService namingService;

    @Override
    public Optional<ChatChannel> fetchOrCreateProfileChannelInfo(ChatUser authUser, Long authProfileId, Long targetProfileId) {

        if (authProfileId.equals(targetProfileId))
            throw new InteractionWithSelfException("cannot start profile channel with yourself");

        //auth profile check for validity
        Optional<ChatProfile> optionalAuthProfile = profilesRepository.findChatProfileByIdWithOwner(authProfileId);
        if (optionalAuthProfile.isEmpty())
            throw new NoSuchEntityException("profile " + authProfileId + " does not exist");
        ChatProfile authProfile = optionalAuthProfile.get();
        if (!authProfile.getOwner().getId().equals(authUser.getId()))
            throw new NoPermissionException("profile " + authProfileId + " does not belong to user " + authUser.getId());
        if (!authProfile.isInteractable())
           throw new RemovedEntityException("profile " + authProfileId + " has been removed");



        Optional<ChatProfile> optionalTargetProfile = profilesRepository.findChatProfileByIdWithOwner(targetProfileId);
        if (optionalTargetProfile.isEmpty())
            throw new NoSuchEntityException("profile " + targetProfileId + " does not exist");
        ChatProfile targetProfile = optionalTargetProfile.get();
        //target existence

        if(authProfile.getOwner().getId().equals(targetProfile.getOwner().getId()))
            throw new InteractionWithSelfException("cannot start profile channel with yourself");
        //same user check

        if (userUtils.checkIfBlockIsPresent(authUser, targetProfile.getOwner().getId()))
            throw new NoPermissionException("user " + authProfile.getId() + " is blocked by user " + targetProfile.getOwner().getId());
        //block check done

        Optional<ChatChannel> optionalExistingChannel = getExistingProfileChannelNoChecks(authUser, authProfileId, targetProfile.getOwner().getId(), targetProfileId);
        //if optional exists and is OK, check for blocks and return
        //if optional exists but is not active, check for target profile and user validity, then blocks, then return
        //if optional doesn't exist, check for target profile and user validity, then blocks, then create and return


        if (optionalExistingChannel.isPresent())
            if (optionalExistingChannel.get().getStatus().equals(ChannelStatus.ACTIVE))
                return optionalExistingChannel;
        //existing active return

        if (!targetProfile.isInteractable())
            throw new RemovedEntityException("profile " + targetProfileId + " has been removed");
        if (!targetProfile.getProfileVisibilityMatchmaking().equals(ProfileVisibilityMatchmaking.VISIBLE))
            throw new BadTargetException("profile " + targetProfileId + " does not accept profile channels");
        if (!targetProfile.getOwner().isInteractable())
            throw new RemovedEntityException("owner of profile " + targetProfileId + " has been removed");
        //target validity done

        if (optionalExistingChannel.isPresent()) {
            if (!optionalExistingChannel.get().getStatus().equals(ChannelStatus.EMPTY)) {
                //TODO this is probably illegal state
                optionalExistingChannel.get().setStatus(ChannelStatus.ACTIVE); //EMPTY channels do not become active from fetch, only when posting
                channelRepository.saveAndFlush(optionalExistingChannel.get());
            }
            return optionalExistingChannel;
        }
        //existing channel code done

        ChatChannel newChannel = createNewProfileChannel(authProfile, targetProfile);
        entityManager.detach(newChannel);
        return channelRepository.findChatChannelByIdWithMembershipsWithProfileAndUser(newChannel.getId());
    }

    private Optional<ChatChannel> getExistingProfileChannelNoChecks(ChatUser authUser,  Long authProfileId, Long targetUserId, Long targetProfileId) {

        Optional<Long> channelId =  authUser.getChannelMemberships().values().stream()
                .map(ChatChannelMembership::getChannel)
                .filter(c -> c.getChannelType().equals(ChannelType.PRIVATE_CHAT_FROM_PROFILE))
                .filter(c ->
                        c.getChannelName().equals(namingService.generateProfileChannelName(authUser.getId(), targetUserId, authProfileId, targetProfileId)))
                .map(ChatChannel::getId)
                .findFirst();
        if (channelId.isEmpty())
            return Optional.empty();

        return channelRepository.findChatChannelByIdWithMembershipsWithProfileAndUser(channelId.get());

    }
    private ChatChannel createNewProfileChannel(ChatProfile profile1, ChatProfile profile2) {
        ChatChannel newChannel = ChatChannel.builder()
                .channelName(namingService.generateProfileChannelName(profile1.getOwner().getId(), profile2.getOwner().getId(), profile1.getId(), profile2.getId()))
                .channelType(ChannelType.PRIVATE_CHAT_FROM_PROFILE)
                .owner(profile1.getOwner())
                .status(ChannelStatus.EMPTY)
                .build();

        newChannel = channelRepository.saveAndFlush(newChannel);
        membershipService.createNewMembershipAndSaveNoCheck(
                profile1.getOwner(), newChannel, Optional.of(profile1), ChannelMembershipRole.PROFILE_CHANNEL_ACCESS
        );
        membershipService.createNewMembershipAndSaveNoCheck(
                profile2.getOwner(), newChannel, Optional.of(profile2), ChannelMembershipRole.PROFILE_CHANNEL_ACCESS
        );

        return newChannel;
    }


}
