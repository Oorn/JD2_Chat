package com.andrey.controller;

import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.db_entities.chat_profile.ChatProfileRepository;
import com.andrey.db_entities.chat_profile.ProfileStatus;
import com.andrey.db_entities.chat_profile.ProfileVisibilityMatchmaking;
import com.andrey.db_entities.chat_profile.ProfileVisibilityUserInfo;
import com.andrey.repository.user.UserRepositoryInterface;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.db_entities.chat_user.ChatUserRepository;
import com.andrey.db_entities.chat_user.UserCredentials;
import com.andrey.db_entities.chat_user.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class TestRestController {

    private final UserRepositoryInterface userRepositoryInterface;

    private final ChatUserRepository chatUserRepository;
    private final ChatProfileRepository chatProfileRepository;

    @GetMapping("/ping")
    public ResponseEntity<Object> ping(){
        return new ResponseEntity<>(chatUserRepository.findChatUserById(1L), HttpStatus.OK);
    }
    @PostMapping("/postTest")
    public ResponseEntity<Object> post(@RequestBody String testBody){
        ChatUser newUser = ChatUser.builder()
                .userName("test user "+ testBody)
                .credentials(UserCredentials.builder().email("testMail " + UUID.randomUUID()).passwordHash("testPass").build())
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
}
