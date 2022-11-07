package com.andrey.controller;

import com.andrey.controller.responses.BlockListResponse;
import com.andrey.controller.responses.PendingInvitesListResponse;
import com.andrey.controller.responses.ProfileInfoFullResponse;
import com.andrey.controller.responses.UserInfoResponse;
import com.andrey.controller.responses.UserMembershipsWithLastUpdateResponse;
import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.exceptions.IllegalStateException;
import com.andrey.security.AuthenticatedChatUserDetails;
import com.andrey.security.jwt.JWTPropertiesConfig;
import com.andrey.service.channel_invite.ChannelInviteService;
import com.andrey.service.profile.ProfilesService;
import com.andrey.service.user.ChatUserService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    @Lazy
    private final ConversionService converter;

    private final ChannelInviteService inviteService;

    private final ChatUserService userService;

    private final ProfilesService profilesService;

    @GetMapping("/info")
    @Operation(summary = "returns user information visible for currently authenticated user", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> userInfo(@RequestParam @Positive Long userId
            , @Parameter(hidden = true) Authentication auth) {

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        Optional<ChatUser> optionalResult = userService.getUserInfoForViewer(userId, authUser);
        if (optionalResult.isEmpty())
            throw new IllegalStateException("service returned empty Optional");

        UserInfoResponse response = converter.convert(optionalResult.get(), UserInfoResponse.class);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/myChannelInfo")
    @Operation(summary = "returns information about channels that user is member of, including update dates", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> getChannelUpdates(@Parameter(hidden = true) Authentication auth) {

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        UserMembershipsWithLastUpdateResponse response = converter.convert(authUser, UserMembershipsWithLastUpdateResponse.class);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/myInvites")
    @Operation(summary = "returns currently pending invites to multiuser channels", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> getChannelInvites(@Parameter(hidden = true) Authentication auth) {

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        PendingInvitesListResponse response = converter.convert(inviteService.getPendingChannelInvites(authUser), PendingInvitesListResponse.class);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/myProfiles")
    @Operation(summary = "returns info about authenticated user profiles", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> myProfiles(@Parameter(hidden = true) Authentication auth){

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        List<ChatProfile> result = profilesService.getOwnedProfiles(authUser);
        List<ProfileInfoFullResponse> response = result.stream()
                .map(p -> converter.convert(p, ProfileInfoFullResponse.class))
                .collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/myBlocks")
    @Operation(summary = "returns information about users you are currently blocked from interacting with", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> userInfo(@Parameter(hidden = true) Authentication auth) {

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        BlockListResponse response = converter.convert(authUser, BlockListResponse.class);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
