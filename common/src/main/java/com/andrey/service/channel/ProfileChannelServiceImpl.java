package com.andrey.service.channel;

import com.andrey.db_entities.chat_channel.ChannelStatus;
import com.andrey.db_entities.chat_channel.ChannelType;
import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_channel_membership.ChannelMembershipRole;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.db_entities.chat_profile.ProfileVisibilityMatchmaking;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.repository.ChatChannelRepository;
import com.andrey.repository.ChatProfileRepository;
import com.andrey.service.blocks.BlockService;
import com.andrey.service.membership.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileChannelServiceImpl implements ProfileChannelService {
    private final ChatProfileRepository profilesRepository;

    private final ChatChannelRepository channelRepository;

    private final BlockService blockService;

    private final MembershipService membershipService;

    private final EntityManager entityManager;

    @Override
    public Optional<ChatChannel> fetchOrCrateProfileChannelInfo(ChatUser authUser, Long authProfileId, Long targetProfileId) {

        if (authProfileId.equals(targetProfileId))
            return Optional.empty();

        //auth profile check for validity
        Optional<ChatProfile> optionalAuthProfile = profilesRepository.findChatProfileByIdWithOwner(authProfileId);
        if (optionalAuthProfile.isEmpty())
            return Optional.empty();
        ChatProfile authProfile = optionalAuthProfile.get();
        if (!authProfile.getOwner().getId().equals(authUser.getId()))
            return Optional.empty();
        if (!authProfile.isInteractable())
            return Optional.empty();

        Optional<ChatChannel> optionalExistingChannel = getExistingProfileChannelNoChecks(authUser, authProfileId, targetProfileId);
        //if optional exists and is OK, check for blocks and return
        //if optional exists but is not active, check for target profile and user validity, then blocks, then return
        //if optional doesn't exist, check for target profile and user validity, then blocks, then create and return

        Optional<ChatProfile> optionalTargetProfile = profilesRepository.findChatProfileByIdWithOwner(targetProfileId);
        if (optionalTargetProfile.isEmpty())
            return Optional.empty();
        ChatProfile targetProfile = optionalTargetProfile.get();
        //target existence

        if(authProfile.getOwner().getId().equals(targetProfile.getOwner().getId()))
            return Optional.empty();
        //same user check

        if (blockService.fetchAndCheckIfBLockIsPresent(authProfile.getOwner(), targetProfile.getOwner()))
            return Optional.empty();
        //block check done

        if (optionalExistingChannel.isPresent())
            if (optionalExistingChannel.get().getStatus().equals(ChannelStatus.ACTIVE))
                return optionalExistingChannel;
        //existing active return

        if (!targetProfile.isInteractable())
            return Optional.empty();
        if (!targetProfile.getProfileVisibilityMatchmaking().equals(ProfileVisibilityMatchmaking.VISIBLE))
            return Optional.empty();
        if (!targetProfile.getOwner().isInteractable())
            return Optional.empty();
        //target validity done

        if (optionalExistingChannel.isPresent()) {
            if (!optionalExistingChannel.get().getStatus().equals(ChannelStatus.EMPTY)) {
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

    private Optional<ChatChannel> getExistingProfileChannelNoChecks(ChatUser authUser, Long authProfileId, Long targetProfileId) {

        Optional<Long> channelId =  authUser.getChannelMemberships().values().stream()
                .map(ChatChannelMembership::getChannel)
                .filter(c -> c.getChannelType().equals(ChannelType.PRIVATE_CHAT_FROM_PROFILE))
                .filter(c ->
                        c.getChannelName().equals(generateProfileChannelName(authProfileId, targetProfileId)))
                .map(ChatChannel::getId)
                .findFirst();
        if (channelId.isEmpty())
            return Optional.empty();

        return channelRepository.findChatChannelByIdWithMembershipsWithProfileAndUser(channelId.get());

    }
    private ChatChannel createNewProfileChannel(ChatProfile profile1, ChatProfile profile2) {
        ChatChannel newChannel = ChatChannel.builder()
                .channelName(generateProfileChannelName(profile1.getId(), profile2.getId()))
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

    private String generateProfileChannelName(long profileId1, long profileId2) {
        if (profileId1 > profileId2) {
            long t = profileId1;
            profileId1 = profileId2;
            profileId2 = t;
        }
        return "profile_chat_" + profileId1 + "_" + profileId2;
    }
}
