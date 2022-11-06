package com.andrey.service.membership;

import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_channel_membership.ChannelMembershipRole;
import com.andrey.db_entities.chat_channel_membership.ChannelMembershipStatus;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.repository.ChatChannelMembershipRepository;
import com.andrey.service.cached_user_details.CachedUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService{

    private final ChatChannelMembershipRepository membershipRepository;

    private final CachedUserDetailsService cacheUserService;

    @Override
    public ChatChannelMembership createNewMembershipAndSaveNoCheck(ChatUser user, ChatChannel channel, Optional<ChatProfile> optionalProfile, ChannelMembershipRole role) {

        ChatChannelMembership res = ChatChannelMembership.builder()
                .user(user)
                .channel(channel)
                .channelId(channel.getId())
                .role(role)
                .status(ChannelMembershipStatus.ACTIVE)
                .build();
        optionalProfile.ifPresent(res::setUserProfile);

        ChatChannelMembership result = membershipRepository.saveAndFlush(res);
        cacheUserService.evictUserFromCache(user);
        return result;
    }
}
