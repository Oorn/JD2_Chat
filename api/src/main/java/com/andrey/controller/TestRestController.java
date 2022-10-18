package com.andrey.controller;

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
}
