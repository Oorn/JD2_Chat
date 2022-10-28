package com.andrey.service.message;

import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_message.ChatMessage;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.repository.ChatMessageRepository;
import com.andrey.service.MessageUpdateDatePropagateService;
import com.andrey.service.user.ChatUserUtilsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessagesServiceImpl implements MessagesService{

    private final ChatUserUtilsService userUtils;
    private final MessageUpdateDatePropagateService propagateService;
    private final ChatMessageRepository messageRepository;

    private final EntityManager entityManager;

    @Override
    public Optional<ChatMessage> createMessage(ChatUser authUser, ChatMessage message, long channelId) {
        if (!userUtils.checkIfAuthUserCanPostInChannel(authUser, channelId))
            return Optional.empty();

        ChatChannel channel = authUser.getChannelMemberships().get(channelId).getChannel();
        message.setChannel(channel);
        message.setSender(authUser);

        messageRepository.saveAndFlush(message);
        entityManager.detach(message);

        message = messageRepository.findChatMessageByIdWithChannelWithMembersWithUsers(message.getId());
        propagateService.updateDateAndPropagate(message);

        return Optional.of(message);
    }
}
