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

    //PROFILES

    public static final int MIN_PROFILE_NAME_LENGTH = 4;

    public static final int MAX_PROFILE_NAME_LENGTH = 100;

    public static final int MIN_PROFILE_DESCRIPTION_LENGTH = 4;

    public static final int MAX_PROFILE_DESCRIPTION_LENGTH = 10000;

    public static final int MIN_ACCEPTED_PROFILE_FORMAT = 1;

    public static final int MAX_ACCEPTED_PROFILE_FORMAT = 1;

    public static final int MAX_PROFILES_PER_USER = 20;

    public static final int MAX_MATCHMAKING_PROFILES_PER_RESPONSE = 20;

    public static final int MIN_MESSAGE_LENGTH = 1;

    public static final int MAX_MESSAGE_LENGTH = 5000;

    public static final int MIN_ACCEPTED_MESSAGE_FORMAT = 1;

    public static final int MAX_ACCEPTED_MESSAGE_FORMAT = 1;
}
