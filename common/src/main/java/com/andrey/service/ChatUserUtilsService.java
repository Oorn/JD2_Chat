package com.andrey.service;

import com.andrey.db_entities.chat_user.ChatUser;

public interface ChatUserUtilsService {
    long getActiveProfileNumber (ChatUser user);
}
