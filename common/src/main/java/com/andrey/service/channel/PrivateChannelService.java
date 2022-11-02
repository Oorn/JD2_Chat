package com.andrey.service.channel;

import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_user.ChatUser;

import java.util.Optional;

public interface PrivateChannelService {

    Optional<ChatChannel> fetchOrCreatePrivateChannelInfo(ChatUser authUser, Long targetUserId);
}
