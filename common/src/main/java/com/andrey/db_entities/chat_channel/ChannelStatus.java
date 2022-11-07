package com.andrey.db_entities.chat_channel;

public enum ChannelStatus {
    EMPTY,   //functions as active, but private and profile chats without messages are usually not returned unless explicitly requested
    ACTIVE,
    REMOVED
}
