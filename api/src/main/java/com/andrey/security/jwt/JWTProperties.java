package com.andrey.security.jwt;

public interface JWTProperties {
    String SECRET =  "6013fba8-50ec-4830-a6b6-1c2b7ef27b3a";
    long TOKEN_LIFETIME_MILLIS = 3600000;//1 hour

    String AUTH_TOKEN_HEADER = "Auth-Token";
}
