package com.andrey.db_entities.chat_channel;

public enum ChannelStatus {
    DEFAULT_CHANNEL_STATUS,
    EMPTY,   //private and profile chats without messages are usually not returned unless explicitly requested
    ACTIVE,
    EXPIRED, //profile chats might have expiration time in the future
    REMOVED
}
