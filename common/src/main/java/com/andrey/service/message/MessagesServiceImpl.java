package com.andrey.service.message;

import com.andrey.Constants;
import com.andrey.db_entities.chat_channel.ChannelStatus;
import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_message.ChatMessage;
import com.andrey.db_entities.chat_message.MessageStatus;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.exceptions.NoPermissionException;
import com.andrey.exceptions.NoSuchEntityException;
import com.andrey.exceptions.RemovedEntityException;
import com.andrey.repository.ChatChannelRepository;
import com.andrey.repository.ChatMessageRepository;
import com.andrey.service.MessageUpdateDatePropagateService;
import com.andrey.service.user.ChatUserUtilsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessagesServiceImpl implements MessagesService{

    private final ChatUserUtilsService userUtils;
    private final MessageUpdateDatePropagateService propagateService;
    private final ChatMessageRepository messageRepository;

    private final ChatChannelRepository channelRepository;

    private final EntityManager entityManager;

    @Override
    public Optional<ChatMessage> createMessage(ChatUser authUser, ChatMessage message, long channelId) {
        if (!userUtils.checkIfAuthUserCanPostInChannel(authUser, channelId))
            throw new NoPermissionException("user " + authUser.getId() + " cannot post in channel " + channelId);

        ChatChannel channel = authUser.getChannelMemberships().get(channelId).getChannel();
        message.setChannel(channel);
        if (channel.getStatus().equals(ChannelStatus.EMPTY)) {
            channel.setStatus(ChannelStatus.ACTIVE);
            channelRepository.saveAndFlush(channel);
        }
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
            throw new NoSuchEntityException("message " + message.getId() + " does not exist");
        ChatMessage oldMessage = optionalOldMessage.get();
        if (!oldMessage.getSender().getId().equals(authUser.getId()))
            throw new NoPermissionException("user " + authUser.getId() + " does not own message " + message.getId());
        if (!oldMessage.isInteractable())
            throw new RemovedEntityException("message " + message.getId() + " has been removed");
        if (!userUtils.checkIfAuthUserCanPostInChannel(authUser, oldMessage.getChannel().getId()))
            throw new NoPermissionException("user " + authUser.getId() + "cannot post in channel " + oldMessage.getChannel().getId());

        oldMessage.setFormatVersion(message.getFormatVersion());
        oldMessage.setMessageBody(message.getMessageBody());

        messageRepository.saveAndFlush(oldMessage);
        propagateService.updateDateAndPropagate(oldMessage);

        return Optional.of(oldMessage);
    }

    @Override
    public Optional<ChatMessage> deleteMessage(ChatUser authUser, long messageId) {
        Optional<ChatMessage> optionalOldMessage = messageRepository.findChatMessageByIdWithChannelWithMembersWithUsers(messageId);

        if (optionalOldMessage.isEmpty())
            throw new NoSuchEntityException("message " + messageId + " does not exist");
        ChatMessage oldMessage = optionalOldMessage.get();
        if ((!oldMessage.getSender().getId().equals(authUser.getId()))
                && !userUtils.checkIfAuthUserCanModerateChannel(authUser, oldMessage.getChannel().getId()))
            throw new NoPermissionException("user " + authUser.getId() + " has no delete access to message " + messageId);
        if (!oldMessage.isInteractable())
            throw new RemovedEntityException("message " + messageId + " has been removed");
        if (!userUtils.checkIfAuthUserCanPostInChannel(authUser, oldMessage.getChannel().getId()))
            throw new NoPermissionException("user " + authUser.getId() + "cannot post in channel " + oldMessage.getChannel().getId());

        oldMessage.setStatus(MessageStatus.REMOVED);

        messageRepository.saveAndFlush(oldMessage);
        propagateService.updateDateAndPropagate(oldMessage);

        return Optional.of(oldMessage);
    }

    @Override
    public List<ChatMessage> getLatestMessages(ChatUser authUser, long channelId) {
        if (!userUtils.checkIfAuthUserCanReadInChannel(authUser, channelId))
            throw new NoPermissionException("user "+ authUser.getId() + " cannot read messages in channel " + channelId);

        return messageRepository.getLatestChatMessagesByChannelId(channelId, Constants.MAX_MESSAGES_PER_RESPONSE);
    }

    @Override
    public List<ChatMessage> getMessagesBeforeId(ChatUser authUser, long channelId, long messageId) {
        if (!userUtils.checkIfAuthUserCanReadInChannel(authUser, channelId))
            throw new NoPermissionException("user "+ authUser.getId() + " cannot read messages in channel " + channelId);

        return messageRepository.getChatMessagesBeforeMessageIdByChannelId(channelId, messageId, Constants.MAX_MESSAGES_PER_RESPONSE);
    }

    @Override
    public List<ChatMessage> getMessagesAfterId(ChatUser authUser, long channelId, long messageId) {
        if (!userUtils.checkIfAuthUserCanReadInChannel(authUser, channelId))
            throw new NoPermissionException("user "+ authUser.getId() + " cannot read messages in channel " + channelId);

        return messageRepository.getChatMessagesAfterMessageIdByChannelId(channelId, messageId, Constants.MAX_MESSAGES_PER_RESPONSE);
    }

    @Override
    public List<ChatMessage> getMessagesUpdatesAfterIdTimestamp(ChatUser authUser, long channelId, long messageId, Timestamp timestamp) {
        if (!userUtils.checkIfAuthUserCanReadInChannel(authUser, channelId))
            throw new NoPermissionException("user "+ authUser.getId() + " cannot read messages in channel " + channelId);

        return messageRepository.getChatUpdatesAfterMessageIdAndTimestampByChannelId(channelId, messageId, timestamp, Constants.MAX_MESSAGES_PER_RESPONSE);
    }
}
