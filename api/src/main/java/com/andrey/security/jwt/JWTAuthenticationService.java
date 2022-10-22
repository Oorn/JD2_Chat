package com.andrey.security.jwt;

import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.db_entities.chat_user.ChatUserRepository;
import com.andrey.db_entities.chat_user.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JWTAuthenticationService implements UserDetailsService {

    private final ChatUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            /*Find user in DB*/
            ChatUser searchResult = userRepository.findChatUserByEmail(email);

            if (searchResult != null) {

                return new org.springframework.security.core.userdetails.User(
                        searchResult.getEmail(),
                        searchResult.getPasswordHash(),
                        AuthorityUtils.NO_AUTHORITIES
                );
            } else {
                throw new UsernameNotFoundException(String.format("No user found with login '%s'.", email));
            }
        } catch (Exception e) {
            throw new UsernameNotFoundException("User with this login not found");
        }
    }


}
