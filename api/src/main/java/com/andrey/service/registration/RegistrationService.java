package com.andrey.service.registration;

import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.requests.ChatUserCreateRequest;
import com.andrey.requests.ConfirmEmailRequest;
import com.andrey.requests.ResetPasswordRequest;

import java.util.Optional;

public interface RegistrationService {
    Optional<ChatUser> createNewUser(ChatUserCreateRequest request);
    boolean confirmEmail(ConfirmEmailRequest request);
    Optional<String> createPasswordResetToken(String userEmail);
    boolean resetPassword(ResetPasswordRequest request);
}
