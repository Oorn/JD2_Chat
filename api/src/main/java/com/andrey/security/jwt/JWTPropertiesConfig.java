package com.andrey.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("jwt")
@Data
public class JWTPropertiesConfig {
    private String secret; // = "6013fba8-50ec-4830-a6b6-1c2b7ef27b3a";
    private Long expiration;// = 3600000L;//1 hour

    static final String AUTH_TOKEN_HEADER = "Auth-Token";
}


