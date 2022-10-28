package com.andrey.service.membership;

import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_channel_membership.ChannelMembershipRole;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.db_entities.chat_user.ChatUser;

import java.util.Optional;

public interface MembershipService {
    ChatChannelMembership createNewMembershipAndSaveNoCheck(ChatUser user
            , ChatChannel channel, Optional<ChatProfile> optionalProfile
            , ChannelMembershipRole role);
}
