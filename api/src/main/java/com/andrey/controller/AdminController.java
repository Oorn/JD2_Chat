package com.andrey.controller;

import com.andrey.controller.requests.ChatUserCreateRequest;
import com.andrey.controller.requests.ConfirmEmailRequest;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.service.mail.MailSenderService;
import com.andrey.service.registration.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final RegistrationService registrationService;

    @Autowired
    @Lazy
    private ConversionService converter;

    @PostMapping("/createNewUser")
    @Transactional
    @Operation(summary = "creates user with given credentials and skips verification")
    public ResponseEntity<Object> createNewUser(@RequestBody @Valid ChatUserCreateRequest createRequest){

        ChatUser newUser = converter.convert(createRequest, ChatUser.class);

        Optional<ChatUser> optionalUser = registrationService.createNewUser(newUser);

        if (optionalUser.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        ChatUser user = optionalUser.get();
        registrationService.confirmEmail(new ConfirmEmailRequest(user.getEmail(),  user.getEmailConfirmationToken()));

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
