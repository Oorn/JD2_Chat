package com.andrey.test_controller;

import com.andrey.repository.ChatFriendshipRepository;
import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.repository.ChatProfileRepository;
import com.andrey.db_entities.chat_profile.ProfileStatus;
import com.andrey.db_entities.chat_profile.ProfileVisibilityMatchmaking;
import com.andrey.db_entities.chat_profile.ProfileVisibilityUserInfo;
import com.andrey.test_repository.user.UserRepositoryInterface;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.repository.ChatUserRepository;
import com.andrey.db_entities.chat_user.UserStatus;
import com.andrey.security.jwt.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class TestRestController {

    private final UserRepositoryInterface userRepositoryInterface;

    private final ChatUserRepository chatUserRepository;

    private final ChatProfileRepository profileRepository;
    private final ChatProfileRepository chatProfileRepository;

    private final JWTUtils tokenUtils;

    private final ChatFriendshipRepository friendshipRepository;

    @GetMapping("public/ping")
    public ResponseEntity<Object> ping(){

        List<ChatProfile> l = profileRepository.getRandomMatchmakingProfilesForUserWithSample(5, 43L, 10);
        return new ResponseEntity<>(l, HttpStatus.OK);
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
                .owner(chatUserRepository.findChatUserById(1L).get())
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

    @Transactional
    @GetMapping("public/testFriendship")
    public ResponseEntity<Object> testFriendshipAuth(){
        ChatUser user1 = chatUserRepository.findChatUserById(1L).get();
        ChatUser user2 = chatUserRepository.findChatUserByEmail("jd2_chat@proton.me");
        friendshipRepository.createFriendship(user1, user2);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
