package com.andrey.service.message;

import com.andrey.db_entities.chat_message.ChatMessage;
import com.andrey.db_entities.chat_user.ChatUser;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface MessagesService {
    Optional<ChatMessage> createMessage(ChatUser authUser, ChatMessage message, long channelId);

    Optional<ChatMessage> updateMessage(ChatUser authUser, ChatMessage message);

    List<ChatMessage> getLatestMessages(ChatUser authUser, long channelId);

    List<ChatMessage> getMessagesBeforeId(ChatUser authUser, long channelId, long messageId);

    List<ChatMessage> getMessagesAfterId(ChatUser authUser, long channelId, long messageId);

    List<ChatMessage> getMessagesUpdatesAfterIdTimestamp(ChatUser authUser, long channelId, long messageId, Timestamp timestamp);

}
