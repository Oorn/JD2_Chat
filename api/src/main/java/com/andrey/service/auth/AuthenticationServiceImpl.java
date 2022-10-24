package com.andrey.service.auth;

import com.andrey.db_entities.PasswordEncryptionConfiguration;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.db_entities.chat_user.ChatUserRepository;
import com.andrey.db_entities.chat_user.UserStatus;
import com.andrey.controller.requests.AuthenticationRequest;
import com.andrey.security.jwt.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService{

    private final ChatUserRepository userRepository;

    private final PasswordEncoder encryptor;

    private final JWTUtils utils;

    private boolean checkIfTokenCanBeRefreshedForEmail(String email) throws UsernameNotFoundException {
        try {
            /*Find user in DB*/
            ChatUser searchResult = userRepository.findChatUserByEmail(email);
            return searchResult.getStatus() == UserStatus.OK;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Optional<String> generateAuthToken(AuthenticationRequest request) {
        ChatUser user;
        try {
            user = userRepository.findChatUserByEmail(request.getEmail());
        }
        catch (Exception e)//TODO - add sensible exceptions and deny reason tracking
        {
            return Optional.empty();
        }
        if (user == null)
            return Optional.empty();

        if (!encryptor.matches(request.getPassword(), user.getPasswordHash()))
            return Optional.empty();

        if (!user.getStatus().equals(UserStatus.OK))
            return Optional.empty();

        UserDetails ud = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                AuthorityUtils.NO_AUTHORITIES
        );

        return Optional.of(utils.generateToken(ud));
    }

    @Override
    public Optional<String> refreshAuthToken(String token) {
        if (!utils.validateToken(token))
            return Optional.empty();

        String email = utils.getUsernameFromToken(token);
        if (!checkIfTokenCanBeRefreshedForEmail(email))
            return Optional.empty();

        return utils.refreshToken(token);
    }


}
