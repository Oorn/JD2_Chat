package com.andrey.service;

import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_message.ChatMessage;
import com.andrey.db_entities.chat_user.ChatUser;

import java.sql.Timestamp;
import java.util.List;

public interface MessageUpdateDatePropagateService {
    Timestamp updateDateAndPropagate(ChatMessage message);

    Timestamp updateDateAndPropagate(ChatChannel channel);

    List<ChatUser> getRecipientUserList(ChatMessage message);
}
