package com.andrey.controller;

import com.andrey.controller.requests.GetMessageUpdatesAfterRequest;
import com.andrey.controller.requests.message_requests.EditMessageRequest;
import com.andrey.controller.requests.message_requests.SendMessageRequest;
import com.andrey.controller.requests.profile_requests.CreateProfileRequest;
import com.andrey.controller.responses.MessageInfoResponse;
import com.andrey.db_entities.chat_message.ChatMessage;
import com.andrey.db_entities.chat_profile.ChatProfile;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.exceptions.IllegalStateException;
import com.andrey.security.AuthenticatedChatUserDetails;
import com.andrey.security.jwt.JWTPropertiesConfig;
import com.andrey.service.message.MessagesService;
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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
@Validated
public class MessageController {

    @Autowired
    @Lazy
    private ConversionService converter;

    private final MessagesService messagesService;

    @PostMapping("/send")
    @Transactional
    @Operation(summary = "sends new message to channel from current user", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> send(@RequestBody @Valid SendMessageRequest messageRequest
            , @Parameter(hidden = true) Authentication auth){

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        ChatMessage newMessage = converter.convert(messageRequest, ChatMessage.class);
        Optional<ChatMessage> result = messagesService.createMessage(authUser, newMessage, messageRequest.getChannelId());
        if (result.isEmpty())
            throw new IllegalStateException("service returned empty Optional");

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/edit")
    @Transactional
    @Operation(summary = "edits message sent from current user", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> edit(@RequestBody @Valid EditMessageRequest messageRequest
            , @Parameter(hidden = true) Authentication auth){

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        ChatMessage newMessage = converter.convert(messageRequest, ChatMessage.class);
        Optional<ChatMessage> result = messagesService.updateMessage(authUser,newMessage);
        if (result.isEmpty())
            throw new IllegalStateException("service returned empty Optional");

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    @Transactional
    @Operation(summary = "deletes message sent by current user, or by another user in Channel where current user has sufficient Role", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> edit(@RequestParam @Positive Long deleteId
            , @Parameter(hidden = true) Authentication auth){

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        Optional<ChatMessage> result = messagesService.deleteMessage(authUser, deleteId);
        if (result.isEmpty())
            throw new IllegalStateException("service returned empty Optional");

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/getLatest")
    @Operation(summary = "gets latest messages from channel", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> getLatest(@RequestParam @Positive Long channelId
            , @Parameter(hidden = true) Authentication auth){

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        List<ChatMessage> result = messagesService.getLatestMessages(authUser, channelId);
        if (result == null)
            throw new IllegalStateException("service returned null");
        List<MessageInfoResponse> response =
                result.stream().map(m -> converter.convert(m, MessageInfoResponse.class))
                        .collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getBefore")
    @Operation(summary = "gets messages from channel before message with id param", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> getBefore(@RequestParam @Positive Long channelId
            ,  @RequestParam @Positive Long beforeId
            , @Parameter(hidden = true) Authentication auth){

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        List<ChatMessage> result = messagesService.getMessagesBeforeId(authUser, channelId, beforeId);
        if (result == null)
            throw new IllegalStateException("service returned null");
        List<MessageInfoResponse> response =
                result.stream().map(m -> converter.convert(m, MessageInfoResponse.class))
                        .collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getAfter")
    @Operation(summary = "gets messages from channel after message with id param", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> getAfter(@RequestParam @Positive Long channelId
            ,  @RequestParam @Positive Long afterId
            , @Parameter(hidden = true) Authentication auth){

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        List<ChatMessage> result = messagesService.getMessagesAfterId(authUser, channelId, afterId);
        if (result == null)
            throw new IllegalStateException("service returned null");
        List<MessageInfoResponse> response =
                result.stream().map(m -> converter.convert(m, MessageInfoResponse.class))
                        .collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/getUpdatesAfter")
    @Operation(summary = "gets all message updates from channel after given Timestamp, and after given messageId if updateDate is equal to Timestamp", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> getUpdatesAfter(@RequestBody @Valid GetMessageUpdatesAfterRequest updateRequest
            , @Parameter(hidden = true) Authentication auth){

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        List<ChatMessage> result = messagesService.getMessagesUpdatesAfterIdTimestamp(authUser
                , updateRequest.getChannelId()
                , updateRequest.getAfterMessageId()
                , updateRequest.getAfterMessageTimestamp());
        if (result == null)
            throw new IllegalStateException("service returned null");
        List<MessageInfoResponse> response =
                result.stream().map(m -> converter.convert(m, MessageInfoResponse.class))
                        .collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
