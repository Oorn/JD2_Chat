package com.andrey.service.channel;

import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.exceptions.NoPermissionException;
import com.andrey.repository.ChatChannelRepository;
import com.andrey.service.user.ChatUserUtilsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeneralChannelServiceImpl implements GeneralChannelService{

    private final ChatChannelRepository channelRepository;

    private final ChatUserUtilsService userUtils;

    public Optional<ChatChannel> getChannelInfo(ChatUser authUser, long channelId) {
        if (!userUtils.checkIfAuthUserCanReadInChannel(authUser, channelId))
            throw new NoPermissionException("user " + authUser.getId() + " cannot view info of channel " + channelId);

        return channelRepository.findChatChannelByIdWithMembershipsWithProfileAndUser(channelId);

    }
}
