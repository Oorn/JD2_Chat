package com.andrey.service.registration;

import com.andrey.Constants;
import com.andrey.db_entities.PasswordEncryptionConfiguration;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.db_entities.chat_user.ChatUserRepository;
import com.andrey.db_entities.chat_user.UserStatus;
import com.andrey.requests.ChatUserCreateRequest;
import com.andrey.requests.ConfirmEmailRequest;
import com.andrey.requests.ResetPasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService{
    private final ChatUserRepository userRepository;

    private final PasswordEncryptionConfiguration encryptor;

    private void reclaimUserEmail(ChatUser oldUser) {
        oldUser.setEmail(oldUser.getEmail() + " " + UUID.randomUUID());
        oldUser.setStatusReason("reclaimed during create newUserRequest at " + new Date() + " from status " + oldUser.getStatus());
        oldUser.setStatus(UserStatus.EMAIL_RECLAIMED);
    }

    @Override
    public Optional<ChatUser> createNewUser(ChatUserCreateRequest request) {

        ChatUser newUser;

        if (Boolean.TRUE.equals(userRepository.existsByEmail(request.getEmail()))) {
            newUser = userRepository.findChatUserByEmail(request.getEmail());
            if (newUser.getStatus().equals(UserStatus.REMOVED) //registering from previously deleted email
            || (newUser.getStatus().equals(UserStatus.REQUIRES_EMAIL_CONFIRMATION) &&
                    newUser.getEmailConfirmationTokenExpires().before(new Date()))) //registering from email that wasn't confirmed in time
                reclaimUserEmail(newUser);
            else
                return Optional.empty(); //registering from active email impossible
            //TODO more verbose response for registering on active email
        }

        newUser =
                ChatUser.builder()
                        .userName(request.getUsername())
                        .email(request.getEmail())
                        .passwordHash(encryptor.passwordEncoder().encode(request.getPassword()))
                        .status(UserStatus.REQUIRES_EMAIL_CONFIRMATION)
                        .uuid(String.valueOf(UUID.randomUUID()))
                        .emailConfirmationToken(String.valueOf(UUID.randomUUID()))
                        .emailConfirmationTokenExpires(new Timestamp(new Date().getTime() + Constants.EMAIL_CONFIRM_TIMEOUT_MILLIS))
                        .build();
        newUser = userRepository.save(newUser);


        return Optional.of(newUser);
    }

    @Override
    public boolean confirmEmail(ConfirmEmailRequest request) {
        try {
            ChatUser user = userRepository.findChatUserByEmail(request.getUserEmail());
            if (user.getEmailConfirmationTokenExpires().before(new Date())) //registration expired
                return false;
            if (!user.getEmailConfirmationToken().equals(request.getConfirmationToken())) //wrong token
                return false;

            user.setStatus(UserStatus.OK);
            user.setStatusReason("confirmed email at " + new Date());

            return true;
        }
        catch (Exception e)//TODO - add sensible exceptions and error handling
        {
            return false;
        }
    }

    @Override
    public Optional<String> createPasswordResetToken(String userEmail) {
        try {
            ChatUser user = userRepository.findChatUserByEmail(userEmail);
            user.setPasswordResetToken(String.valueOf(UUID.randomUUID()));
            user.setPasswordResetTokenExpires(new Timestamp(new Date().getTime() + Constants.PASSWORD_RESET_TIMEOUT_MILLIS));
            return Optional.of(user.getPasswordResetToken());
        }
        catch (Exception e)//TODO - add sensible exceptions and error handling
        {
            return Optional.empty();
        }
    }

    @Override
    public boolean resetPassword(ResetPasswordRequest request) {
        try {
            ChatUser user = userRepository.findChatUserByEmail(request.getUserEmail());
            if (user.getPasswordResetTokenExpires().before(new Date()))
                return false;
            if (!user.getPasswordResetToken().equals(request.getResetToken()))
                return false;

            user.setPasswordHash(encryptor.passwordEncoder().encode(request.getNewPassword()));

            return true;
        }
        catch (Exception e)//TODO - add sensible exceptions and error handling
        {
            return false;
        }
    }
}
