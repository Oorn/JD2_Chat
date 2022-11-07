package com.andrey.controller;

import com.andrey.db_entities.chat_block.ChatBlock;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.exceptions.IllegalStateException;
import com.andrey.security.AuthenticatedChatUserDetails;
import com.andrey.security.jwt.JWTPropertiesConfig;
import com.andrey.service.blocks.BlockService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.constraints.Positive;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/blocks")
@Validated
public class BlockController {

    @Autowired
    @Lazy
    private ConversionService converter;

    private final BlockService blockService;


    @PostMapping("/blockUser")
    @Transactional
    @Operation(summary = "blocks target User from interacting with you", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> blockUser(@RequestParam @Positive Long userId
            , @Parameter(hidden = true) Authentication auth){

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        Optional<ChatBlock> result = blockService.createBlock(authUser, userId);
        if (result.isEmpty())
            throw new IllegalStateException("service returned empty Optional");

        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/unblockUser")
    @Transactional
    @Operation(summary = "unblocks target User you previously blocked", parameters = {
            @Parameter(in = ParameterIn.HEADER
                    , description = "user auth token"
                    , name = JWTPropertiesConfig.AUTH_TOKEN_HEADER
                    , content = @Content(schema = @Schema(type = "string")))
    })
    public ResponseEntity<Object> unblockUser(@RequestParam @Positive Long userId
            , @Parameter(hidden = true) Authentication auth){

        ChatUser authUser = ((AuthenticatedChatUserDetails) auth.getPrincipal()).getChatUser();

        Optional<ChatBlock> result = blockService.removeBlock(authUser, userId);
        if (result.isEmpty())
            throw new IllegalStateException("service returned empty Optional");

        return new ResponseEntity<>(HttpStatus.OK);
    }



}
