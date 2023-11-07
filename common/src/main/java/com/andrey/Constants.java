package com.andrey;

public final class Constants {


    //REGISTRATION
    public static final int MIN_USERNAME_LENGTH = 4;
    public static final int MAX_USERNAME_LENGTH = 100;

    public static final int MIN_PASSWORD_LENGTH = 4;
    public static final int MAX_PASSWORD_LENGTH = 100;
    public static final int MAX_EMAIL_LENGTH= 50;

    public static final long EMAIL_CONFIRM_TIMEOUT_MILLIS = 86400000L; //1 day

    public static final long PASSWORD_RESET_TIMEOUT_MILLIS = 3600000L; //1 hour

    public static final String EXTERNAL_EMAIL = "jd2_chat@mail.lv";

    //public static final String SERVER_ADDRESS = "http://localhost:8081";
    public static final String SERVER_ADDRESS = "http://13.49.138.46:8081/";

    //PROFILES

    public static final int MIN_PROFILE_NAME_LENGTH = 4;

    public static final int MAX_PROFILE_NAME_LENGTH = 100;

    public static final int MIN_PROFILE_DESCRIPTION_LENGTH = 4;

    public static final int MAX_PROFILE_DESCRIPTION_LENGTH = 10000;

    public static final int MIN_ACCEPTED_PROFILE_FORMAT = 1;

    public static final int MAX_ACCEPTED_PROFILE_FORMAT = 1;

    public static final int MAX_PROFILES_PER_USER = 20;

    public static final int MAX_MATCHMAKING_PROFILES_PER_RESPONSE = 20;

    //MESSAGES

    public static final int MIN_MESSAGE_LENGTH = 1;

    public static final int MAX_MESSAGE_LENGTH = 5000;

    public static final int MIN_ACCEPTED_MESSAGE_FORMAT = 1;

    public static final int MAX_ACCEPTED_MESSAGE_FORMAT = 1;

    public static final int MAX_MESSAGES_PER_RESPONSE = 5; //for testing

    //CHANNELS

    public static final int MAX_OWNED_MULTIUSER_CHANNELS = 5;

    public static final int MIN_MULTIUSER_CHANNEL_NAME_LENGTH = 4;

    public static final int MAX_MULTIUSER_CHANNEL_NAME_LENGTH = 100;

    public static final long PRIVATE_INVITE_TO_CHANNEL_EXPIRE_MILLIS = 86400000L; //1 day

    //CACHE

    public static final String AUTH_USER_CACHE_NAME = "authUserCache";

    public static final int AUTH_USER_CACHE_START_SIZE = 100;

    public static final int AUTH_USER_CACHE_MAX_SIZE = 200;

    public static final int AUTH_USER_CACHE_LIFETIME = 100000; //millis
}
