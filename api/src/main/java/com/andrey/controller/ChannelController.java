package com.andrey.controller;

import com.andrey.controller.requests.channel_requests.ChangeUserRoleRequest;
import com.andrey.controller.requests.channel_requests.CreateMultiuserChannelRequest;
import com.andrey.controller.requests.channel_requests.FetchProfileChannelRequest;
import com.andrey.controller.requests.channel_requests.SendPrivateChannelInviteRequest;
import com.andrey.controller.requests.channel_requests.UpdateMultiuserChannelRequest;
import com.andrey.controller.responses.ChannelInfoResponse;
import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_channel_invite.ChatChannelInvite;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.exceptions.IllegalStateException;
import com.andrey.security.AuthenticatedChatUserDetails;
import com.andrey.security.jwt.JWTPropertiesConfig;
import com.andrey.service.channel.GeneralChannelService;
import com.andrey.service.channel.MultiUserChannelService;
import com.andrey.service.channel.PrivateChannelService;
import com.andrey.service.channel.ProfileChannelService;
import com.andrey.service.channel_invite.ChannelInviteService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/channel")
@Validated
public class ChannelController {

    @Autowired
    @Lazy
    private ConversionService converter;

    private final ProfileChannelService profileChannelService;

    private final PrivateChannelService privateChannelService;

    private final GeneralChannelService channelService;

    private final MultiUserChannelService multiUserChannelService;

    private final ChannelInviteService inviteService;

    @PostMapping("/fetchProfileChannel")
    @Transactional
    @Operation(summary = "fetches, and creates if needed, info of profile channel between 2 profiles", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> fetchProfileChannel(@RequestBody @Valid FetchProfileChannelRequest fetchRequest
            , @Parameter(hidden = true) Authentication auth){

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        Optional<ChatChannel> result = profileChannelService.fetchOrCreateProfileChannelInfo(authUser, fetchRequest.getAuthUserProfileId(), fetchRequest.getTargetProfileId());
        if (result.isEmpty())
            throw new IllegalStateException("service returned empty Optional");
        ChannelInfoResponse response = converter.convert(result.get(), ChannelInfoResponse.class);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/fetchPrivateChannel")
    @Transactional
    @Operation(summary = "fetches, and creates if needed, info of private channel between 2 users", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> fetchProfileChannel(@RequestParam @Positive Long targetUserId
            , @Parameter(hidden = true) Authentication auth){

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        Optional<ChatChannel> result = privateChannelService.fetchOrCreatePrivateChannelInfo(authUser, targetUserId);
        if (result.isEmpty())
            throw new IllegalStateException("service returned empty Optional");
        ChannelInfoResponse response = converter.convert(result.get(), ChannelInfoResponse.class);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/info")
    @Operation(summary = "returns channel information visible for currently authenticated user", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> userInfo(@RequestParam @Positive Long channelId
            , @Parameter(hidden = true) Authentication auth) {

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();


        Optional<ChatChannel> optionalResult = channelService.getChannelInfo(authUser, channelId);
        if (optionalResult.isEmpty())
            throw new IllegalStateException("service returned empty Optional");

        ChannelInfoResponse response = converter.convert(optionalResult.get(), ChannelInfoResponse.class);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/newChannel")
    @Transactional
    @Operation(summary = "creates new multiuser channel", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> newChannel(@RequestBody @Valid CreateMultiuserChannelRequest createRequest
            , @Parameter(hidden = true) Authentication auth){

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        ChatChannel newChannel = converter.convert(createRequest,ChatChannel.class);
        Optional<ChatChannel> result = multiUserChannelService.createMultiuserChannel(authUser,newChannel);
        if (result.isEmpty())
            throw new IllegalStateException("service returned empty Optional");

        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PutMapping("/updateChannel")
    @Transactional
    @Operation(summary = "updates multiuser channel info", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> updateChannel(@RequestBody @Valid UpdateMultiuserChannelRequest updateRequest
            , @Parameter(hidden = true) Authentication auth){

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        ChatChannel newChannel = converter.convert(updateRequest,ChatChannel.class);
        Optional<ChatChannel> result = multiUserChannelService.updateMultiuserChannel(authUser,newChannel);
        if (result.isEmpty())
            throw new IllegalStateException("service returned empty Optional");

        return new ResponseEntity<>(HttpStatus.OK);
    }
    @DeleteMapping("/delete")
    @Transactional
    @Operation(summary = "deletes channel owned by current user", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> delete(@RequestParam @Positive Long deleteId
            , @Parameter(hidden = true) Authentication auth){

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        Optional<ChatChannel> result = multiUserChannelService.deleteMultiuserChannel(authUser, deleteId);
        if (result.isEmpty())
            throw new IllegalStateException("service returned empty Optional");

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/changeRole")
    @Transactional
    @Operation(summary = "changes role of a user in multiuser channel", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> changeRole(@RequestBody @Valid ChangeUserRoleRequest roleRequest
            , @Parameter(hidden = true) Authentication auth){

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        Optional<ChatChannelMembership> result = multiUserChannelService.updateUserRole(authUser, roleRequest.getChannelId(), roleRequest.getUserId(), roleRequest.getNewRole());
        if (result.isEmpty())
            throw new IllegalStateException("service returned empty Optional");

        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/inviteUser")
    @Transactional
    @Operation(summary = "invites user to a multiuser channel", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> inviteUser(@RequestBody @Valid SendPrivateChannelInviteRequest inviteRequest
            , @Parameter(hidden = true) Authentication auth){

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        Optional<ChatChannelInvite> result = multiUserChannelService.sendChannelInviteToUser(authUser, inviteRequest.getChannelId(), inviteRequest.getTargetUserId());
        if (result.isEmpty())
            throw new IllegalStateException("service returned empty Optional");

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/acceptPrivateInvite")
    @Transactional
    @Operation(summary = "accepts private invite to a multiuser channel", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> acceptPrivateInvite(@RequestParam @Positive Long inviteId
            , @Parameter(hidden = true) Authentication auth){

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        Optional<ChatChannelMembership> result = inviteService.acceptPersonalChannelInvite(authUser, inviteId);
        if (result.isEmpty())
            throw new IllegalStateException("service returned empty Optional");

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/declinePrivateInvite")
    @Transactional
    @Operation(summary = "declines private invite to a multiuser channel", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> declinePrivateInvite(@RequestParam @Positive Long inviteId
            , @Parameter(hidden = true) Authentication auth){

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        Optional<ChatChannelInvite> result = inviteService.declinePersonalChannelInvite(authUser, inviteId);
        if (result.isEmpty())
            throw new IllegalStateException("service returned empty Optional");

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
