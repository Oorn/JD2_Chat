package com.andrey.test_controller;

import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.db_entities.chat_profile.ChatProfileRepository;
import com.andrey.db_entities.chat_profile.ProfileStatus;
import com.andrey.db_entities.chat_profile.ProfileVisibilityMatchmaking;
import com.andrey.db_entities.chat_profile.ProfileVisibilityUserInfo;
import com.andrey.test_repository.user.UserRepositoryInterface;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.db_entities.chat_user.ChatUserRepository;
import com.andrey.db_entities.chat_user.UserStatus;
import com.andrey.security.jwt.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class TestRestController {

    private final UserRepositoryInterface userRepositoryInterface;

    private final ChatUserRepository chatUserRepository;
    private final ChatProfileRepository chatProfileRepository;

    private final JWTUtils tokenUtils;

    @GetMapping("/ping")
    public ResponseEntity<Object> ping(){
        return new ResponseEntity<>(chatUserRepository.findChatUserById(1L), HttpStatus.OK);
    }
    @GetMapping("/ping2")
    public ResponseEntity<Object> ping2(Principal principal){
        return new ResponseEntity<>(principal.getName(), HttpStatus.OK);
    }
    @PostMapping("public/postTest")
    public ResponseEntity<Object> post(@RequestBody String testBody){
        ChatUser newUser = ChatUser.builder()
                .userName("test user "+ testBody)
                //.credentials(UserCredentials.builder().email("testMail " + UUID.randomUUID()).passwordHash("testPass").build())
                .email("testMail " + UUID.randomUUID())
                .passwordHash("testPass")
                //
                .status(UserStatus.REQUIRES_EMAIL_CONFIRMATION)
                .uuid(String.valueOf(UUID.randomUUID()))
                .build();
        newUser = chatUserRepository.save(newUser);
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }
    @PostMapping("/postTestProfile")
    public ResponseEntity<Object> postProfileT(@RequestBody String testBody){
        ChatProfile newProfile= ChatProfile.builder()
                .owner(chatUserRepository.findChatUserById(1L))
                .profileName("testname "+testBody)
                .profileDescription("testdescription "+testBody)
                .formatVersion(1)
                .profileVisibilityMatchmaking(ProfileVisibilityMatchmaking.VISIBLE)
                .profileVisibilityUserInfo(ProfileVisibilityUserInfo.PUBLIC)
                .status(ProfileStatus.OK)
                .build();
        newProfile = chatProfileRepository.save(newProfile);
        return new ResponseEntity<>(newProfile, HttpStatus.OK);
    }

    @GetMapping("public/getToken")
    public ResponseEntity<Object> testToken1(){
        ChatUser user = chatUserRepository.findChatUserById(1L);
        UserDetails ud = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                AuthorityUtils.NO_AUTHORITIES
        );
        String token = tokenUtils.generateToken(ud);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
