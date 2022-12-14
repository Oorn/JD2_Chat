package com.andrey.db_entities.chat_channel_membership;

public enum ChannelMembershipRole {
    OWNER,
    ADMIN,
    MODERATOR,
    READ_WRITE_INVITE_ACCESS,
    READ_WRITE_ACCESS,
    READ_ACCESS,
    NO_ACCESS, //banned

    PRIVATE_CHANNEL_ACCESS, //same as READ_WRITE for private chats
    PROFILE_CHANNEL_ACCESS //same as READ_WRITE for profile chats
}
