package com.andrey.service.message;

import com.andrey.Constants;
import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_message.ChatMessage;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.repository.ChatMessageRepository;
import com.andrey.service.MessageUpdateDatePropagateService;
import com.andrey.service.user.ChatUserUtilsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
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

        message = messageRepository.findChatMessageByIdWithChannelWithMembersWithUsers(message.getId()).get();
        propagateService.updateDateAndPropagate(message);

        return Optional.of(message);
    }

    @Override
    public Optional<ChatMessage> updateMessage(ChatUser authUser, ChatMessage message) {

        Optional<ChatMessage> optionalOldMessage = messageRepository.findChatMessageByIdWithChannelWithMembersWithUsers(message.getId());
        if (optionalOldMessage.isEmpty())
            return Optional.empty();
        ChatMessage oldMessage = optionalOldMessage.get();
        if (!oldMessage.getSender().getId().equals(authUser.getId()))
            return Optional.empty();
        if (!oldMessage.isInteractable())
            return Optional.empty();
        if (!userUtils.checkIfAuthUserCanPostInChannel(authUser, oldMessage.getChannel().getId()))
            return Optional.empty();

        oldMessage.setFormatVersion(message.getFormatVersion());
        oldMessage.setMessageBody(message.getMessageBody());

        messageRepository.saveAndFlush(oldMessage);
        propagateService.updateDateAndPropagate(oldMessage);

        return Optional.of(oldMessage);
    }

    @Override
    public List<ChatMessage> getLatestMessages(ChatUser authUser, long channelId) {
        if (!userUtils.checkIfAuthUserCanReadInChannel(authUser, channelId))
            return Collections.emptyList(); //todo exception needed

        return messageRepository.getLatestChatMessagesByChannelId(channelId, Constants.MAX_MESSAGES_PER_RESPONSE);
    }

    @Override
    public List<ChatMessage> getMessagesBeforeId(ChatUser authUser, long channelId, long messageId) {
        if (!userUtils.checkIfAuthUserCanReadInChannel(authUser, channelId))
            return Collections.emptyList(); //todo exception needed

        return messageRepository.getChatMessagesBeforeMessageIdByChannelId(channelId, messageId, Constants.MAX_MESSAGES_PER_RESPONSE);
    }

    @Override
    public List<ChatMessage> getMessagesAfterId(ChatUser authUser, long channelId, long messageId) {
        if (!userUtils.checkIfAuthUserCanReadInChannel(authUser, channelId))
            return Collections.emptyList(); //todo exception needed

        return messageRepository.getChatMessagesAfterMessageIdByChannelId(channelId, messageId, Constants.MAX_MESSAGES_PER_RESPONSE);
    }

    @Override
    public List<ChatMessage> getMessagesUpdatesAfterIdTimestamp(ChatUser authUser, long channelId, long messageId, Timestamp timestamp) {
        if (!userUtils.checkIfAuthUserCanReadInChannel(authUser, channelId))
            return Collections.emptyList(); //todo exception needed

        return messageRepository.getChatUpdatesAfterMessageIdAndTimestampByChannelId(channelId, messageId, timestamp, Constants.MAX_MESSAGES_PER_RESPONSE);
    }
}
