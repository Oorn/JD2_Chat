package com.andrey.controller;

import com.andrey.controller.requests.profile_requests.CreateProfileRequest;
import com.andrey.controller.requests.profile_requests.UpdateProfileRequest;
import com.andrey.db_entities.chat_profile.ChatProfile;

import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.security.AuthenticatedChatUserDetails;
import com.andrey.security.jwt.JWTPropertiesConfig;
import com.andrey.service.profile.ProfilesService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    @Lazy
    private ConversionService converter;

    private final ProfilesService profilesService;

    @PostMapping("/createNewProfile")
    @Transactional
    @Operation(summary = "creates new profile for currently authenticated user", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> createNewProfile(@RequestBody @Valid CreateProfileRequest createRequest
            , @Parameter(hidden = true) Authentication auth){

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        ChatProfile newProfile = converter.convert(createRequest, ChatProfile.class);
        Optional<ChatProfile> result = profilesService.createNewProfile(newProfile, authUser);
        if (result.isEmpty())
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PutMapping("/updateProfile")
    @Transactional
    @Operation(summary = "updates profile of currently authenticated user", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> updateProfile(@RequestBody @Valid UpdateProfileRequest updateRequest
            , @Parameter(hidden = true) Authentication auth){

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();
        ChatProfile newProfile = converter.convert(updateRequest, ChatProfile.class);

        Optional<ChatProfile> result = profilesService.updateProfile(newProfile, authUser);
        if (result.isEmpty())
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(HttpStatus.OK);
    }
    @DeleteMapping("/deleteProfile")
    @Transactional
    @Operation(summary = "delete profile of currently authenticated user", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> deleteProfile(@RequestParam @NotBlank @Positive Long deleteId
            , @Parameter(hidden = true) Authentication auth){

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        Optional<ChatProfile> result = profilesService.deleteProfile(deleteId, authUser);
        if (result.isEmpty())
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
