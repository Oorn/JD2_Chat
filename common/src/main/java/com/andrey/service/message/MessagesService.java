package com.andrey.service.message;

import com.andrey.db_entities.chat_message.ChatMessage;
import com.andrey.db_entities.chat_user.ChatUser;

import java.util.Optional;

public interface MessagesService {
    Optional<ChatMessage> createMessage(ChatUser authUser, ChatMessage message, long channelId);

}
