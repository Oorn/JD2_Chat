package com.andrey.controller;

import com.andrey.controller.requests.channel_requests.FetchProfileChannelRequest;
import com.andrey.controller.responses.ChannelInfoResponse;
import com.andrey.controller.responses.ChatFromProfileInfoResponse;
import com.andrey.controller.responses.UserInfoResponse;
import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.exceptions.IllegalStateException;
import com.andrey.security.AuthenticatedChatUserDetails;
import com.andrey.security.jwt.JWTPropertiesConfig;
import com.andrey.service.channel.GeneralChannelService;
import com.andrey.service.channel.ProfileChannelService;
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
import org.springframework.web.bind.annotation.PostMapping;
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
@Validated
public class ChannelController {

    @Autowired
    @Lazy
    private ConversionService converter;

    private final ProfileChannelService profileChannelService;

    private final GeneralChannelService channelService;

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
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        ChatFromProfileInfoResponse response = converter.convert(result.get(), ChatFromProfileInfoResponse.class);

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
}
