package com.andrey.controller;

import com.andrey.controller.requests.ChatUserCreateRequest;
import com.andrey.controller.requests.ConfirmEmailRequest;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.exceptions.IllegalStateException;
import com.andrey.security.jwt.JWTPropertiesConfig;
import com.andrey.service.registration.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    @Operation(summary = "creates user with given credentials and skips verification", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))})
    public ResponseEntity<Object> createNewUser(@RequestBody @Valid ChatUserCreateRequest createRequest
            , @Parameter(hidden = true) Authentication auth){

        ChatUser newUser = converter.convert(createRequest, ChatUser.class);

        Optional<ChatUser> optionalUser = registrationService.createNewUser(newUser);

        if (optionalUser.isEmpty())
            throw new IllegalStateException("service returned empty Optional");
        ChatUser user = optionalUser.get();
        registrationService.confirmEmail(new ConfirmEmailRequest(user.getEmail(),  user.getEmailConfirmationToken()));

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
