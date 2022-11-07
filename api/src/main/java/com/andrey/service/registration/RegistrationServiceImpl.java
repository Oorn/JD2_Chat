package com.andrey.service.registration;

import com.andrey.Constants;
import com.andrey.controller.requests.ConfirmEmailRequest;
import com.andrey.controller.requests.ResetPasswordRequest;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.db_entities.chat_user.UserStatus;
import com.andrey.repository.ChatUserRepository;
import com.andrey.service.cached_user_details.CachedUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService{
    private final ChatUserRepository userRepository;

    private final PasswordEncoder encryptor;

    private final CachedUserDetailsService cachedUserDetailsService;

    private void reclaimUserEmail(ChatUser oldUser) {
        oldUser.setEmail(oldUser.getEmail() + " " + UUID.randomUUID());
        oldUser.setStatusReason("reclaimed on newUserRequest at " + new Date());
        oldUser.setStatus(UserStatus.EMAIL_RECLAIMED);
        userRepository.saveAndFlush(oldUser);
    }


    @Override
    public Optional<ChatUser> createNewUser(ChatUser newUser) {


        if (Boolean.TRUE.equals(userRepository.existsByEmail(newUser.getEmail()))) {
            ChatUser oldUser = userRepository.findChatUserByEmail(newUser.getEmail());
            if (oldUser.getStatus().equals(UserStatus.REMOVED) //registering from previously deleted email
                    || (oldUser.getStatus().equals(UserStatus.REQUIRES_EMAIL_CONFIRMATION) &&
                    oldUser.getEmailConfirmationTokenExpires().before(new Date()))) //registering from email that wasn't confirmed in time
                reclaimUserEmail(oldUser);
            else
                return Optional.empty(); //registering from active email impossible
            //TODO more verbose response for registering on active email
        }


        newUser = userRepository.saveAndFlush(newUser);
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
            if (!user.getStatus().equals(UserStatus.REQUIRES_EMAIL_CONFIRMATION))
                return false; //wrong state for verification

            user.setStatus(UserStatus.OK);
            user.setStatusReason("confirmed email at " + new Date());
            cachedUserDetailsService.evictUserFromCache(user);

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
            if (!user.isInteractable())
                return Optional.empty();
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
            if (!user.isInteractable())
                return false;

            user.setPasswordHash(encryptor.encode(request.getNewPassword()));
            user.setPasswordResetToken(null);
            user.setPasswordResetDate(new Timestamp(new Date().getTime()));
            cachedUserDetailsService.evictUserFromCache(user);

            return true;
        }
        catch (Exception e)//TODO - add sensible exceptions and error handling
        {
            return false;
        }
    }
}
