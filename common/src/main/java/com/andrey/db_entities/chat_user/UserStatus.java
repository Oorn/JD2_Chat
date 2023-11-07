package com.andrey.db_entities.chat_user;

public enum UserStatus {
    REQUIRES_EMAIL_CONFIRMATION,
    OK,
    BANNED,
    REMOVED,
    EMAIL_RECLAIMED //user was deleted, then registered again from same email
}
