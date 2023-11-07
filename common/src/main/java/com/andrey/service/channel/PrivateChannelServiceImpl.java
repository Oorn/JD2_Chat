package com.andrey.service.channel;

import com.andrey.db_entities.chat_channel.ChannelStatus;
import com.andrey.db_entities.chat_channel.ChannelType;
import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_channel_membership.ChannelMembershipRole;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.exceptions.InteractionWithSelfException;
import com.andrey.exceptions.NoPermissionException;
import com.andrey.exceptions.NoSuchEntityException;
import com.andrey.exceptions.RemovedEntityException;
import com.andrey.repository.ChatChannelRepository;
import com.andrey.repository.ChatUserRepository;
import com.andrey.service.membership.MembershipService;
import com.andrey.service.user.ChatUserUtilsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrivateChannelServiceImpl implements PrivateChannelService{

    private final ChatUserRepository userRepository;

    private final ChatChannelRepository channelRepository;

    private final MembershipService membershipService;

    private final EntityManager entityManager;

    private final ChatUserUtilsService userUtils;

    private final ChannelNamingService namingService;
    @Override
    public Optional<ChatChannel> fetchOrCreatePrivateChannelInfo(ChatUser authUser, Long targetUserId) {
        if (authUser.getId().equals(targetUserId))
            throw new InteractionWithSelfException("cannot start private channel with yourself");

           Optional<ChatChannel> optionalExistingChannel = getExistingPrivateChannelNoChecks(authUser, targetUserId);
        //if optional exists and is OK, check for blocks and return
        //if optional exists but is not active, check for target profile and user validity, then blocks, then return
        //if optional doesn't exist, check for target profile and user validity, then blocks, then create and return

        Optional<ChatUser> optionalTargetUser = userRepository.findChatUserById(targetUserId);
        if (optionalTargetUser.isEmpty())
            throw new NoSuchEntityException("user " + targetUserId + " does not exist");
        ChatUser targetUser = optionalTargetUser.get();
        //target existence


        if (userUtils.checkIfBlockIsPresent(authUser, targetUserId))
            throw new NoPermissionException("user " + authUser.getId() + " is blocked by user " + targetUserId);
        //block check done


        if (optionalExistingChannel.isPresent())
            if (optionalExistingChannel.get().getStatus().equals(ChannelStatus.ACTIVE))
                return optionalExistingChannel;
        //existing active return

        if (!targetUser.isInteractable())
            throw new RemovedEntityException("user " + targetUserId + " has been removed");
        //target validity done

        if (optionalExistingChannel.isPresent()) {
            if (!optionalExistingChannel.get().getStatus().equals(ChannelStatus.EMPTY)) {
                //TODO this is probably now illegal state
                optionalExistingChannel.get().setStatus(ChannelStatus.ACTIVE); //EMPTY channels do not become active from fetch, only when posting
                channelRepository.saveAndFlush(optionalExistingChannel.get());
            }
            return optionalExistingChannel;
        }
        //existing channel code done

        //untested, might nor be necessary
        entityManager.merge(authUser);
        ChatChannel newChannel = createNewPrivateChannel(authUser, targetUser);
        //necessary to reload membership info
        entityManager.detach(newChannel);
        return channelRepository.findChatChannelByIdWithMembershipsWithProfileAndUser(newChannel.getId());
    }

    private Optional<ChatChannel> getExistingPrivateChannelNoChecks(ChatUser authUser, Long targetUserId) {

        Optional<Long> channelId =  authUser.getChannelMemberships().values().stream()
                .map(ChatChannelMembership::getChannel)
                .filter(c -> c.getChannelType().equals(ChannelType.PRIVATE_CHAT_BETWEEN_FRIENDS))
                .filter(c ->
                        c.getChannelName().equals(namingService.generatePrivateChannelName(authUser.getId(), targetUserId)))
                .map(ChatChannel::getId)
                .findFirst();
        if (channelId.isEmpty())
            return Optional.empty();

        return channelRepository.findChatChannelByIdWithMembershipsWithProfileAndUser(channelId.get());

    }

    private ChatChannel createNewPrivateChannel(ChatUser user1, ChatUser user2) {
        ChatChannel newChannel = ChatChannel.builder()
                .channelName(namingService.generatePrivateChannelName(user1.getId(), user2.getId()))
                .channelType(ChannelType.PRIVATE_CHAT_BETWEEN_FRIENDS)
                .owner(user1)
                .status(ChannelStatus.EMPTY)
                .build();

        newChannel = channelRepository.saveAndFlush(newChannel);
        membershipService.createNewMembershipAndSaveNoCheck(
                user1, newChannel, Optional.empty(), ChannelMembershipRole.PRIVATE_CHANNEL_ACCESS
        );
        membershipService.createNewMembershipAndSaveNoCheck(
                user2, newChannel, Optional.empty(), ChannelMembershipRole.PRIVATE_CHANNEL_ACCESS
        );

        return newChannel;
    }

}
