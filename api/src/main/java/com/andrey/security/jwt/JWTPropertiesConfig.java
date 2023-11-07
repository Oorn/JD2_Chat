package com.andrey.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("jwt")
@Data
public class JWTPropertiesConfig {
    private String secret;
    private Long expiration;

    public static final String AUTH_TOKEN_HEADER = "Auth-Token";
}


