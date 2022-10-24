package com.andrey.service.registration;

import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.controller.requests.ChatUserCreateRequest;
import com.andrey.controller.requests.ConfirmEmailRequest;
import com.andrey.controller.requests.ResetPasswordRequest;

import java.util.Optional;

public interface RegistrationService {

    @Deprecated
    Optional<ChatUser> createNewUser(ChatUserCreateRequest request);

    Optional<ChatUser> createNewUser(ChatUser request);
    boolean confirmEmail(ConfirmEmailRequest request);
    Optional<String> createPasswordResetToken(String userEmail);
    boolean resetPassword(ResetPasswordRequest request);
}
