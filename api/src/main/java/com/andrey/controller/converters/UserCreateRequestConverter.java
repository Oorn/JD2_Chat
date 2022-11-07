package com.andrey.controller.converters;

import com.andrey.Constants;
import com.andrey.controller.requests.ChatUserCreateRequest;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.db_entities.chat_user.UserServiceRole;
import com.andrey.db_entities.chat_user.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserCreateRequestConverter implements Converter<ChatUserCreateRequest, ChatUser> {

    private final PasswordEncoder encryptor;

    @Override
    public ChatUser convert(ChatUserCreateRequest source) {
        return ChatUser.builder()
                        .userName(source.getUsername())
                        .email(source.getEmail())
                        .passwordHash(encryptor.encode(source.getPassword()))
                        .status(UserStatus.REQUIRES_EMAIL_CONFIRMATION)
                        .uuid(String.valueOf(UUID.randomUUID()))
                        .emailConfirmationToken(String.valueOf(UUID.randomUUID()))
                        .emailConfirmationTokenExpires(new Timestamp(new Date().getTime() + Constants.EMAIL_CONFIRM_TIMEOUT_MILLIS))
                        .userServiceRole(UserServiceRole.USER)
                        .build();
    }
}
