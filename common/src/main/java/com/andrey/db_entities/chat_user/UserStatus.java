package com.andrey.db_entities.chat_user;

public enum UserStatus {
    DEFAULT_STATUS,
    REQUIRES_EMAIL_CONFIRMATION,
    //DEFAULT_PROFILE_NOT_SET,
    OK,
    BANNED,
    REMOVED,
    EMAIL_RECLAIMED //user was deleted, then registered again from same email
}
