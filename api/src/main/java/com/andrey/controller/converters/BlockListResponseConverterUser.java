package com.andrey.controller.converters;

import com.andrey.controller.responses.BlockListResponse;
import com.andrey.controller.responses.ChannelInfoResponse;
import com.andrey.db_entities.chat_block.BlockStatus;
import com.andrey.db_entities.chat_block.ChatBlock;
import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_user.ChatUser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BlockListResponseConverterUser implements Converter<ChatUser, BlockListResponse> {
    @Override
    public BlockListResponse convert(ChatUser source) {
        BlockListResponse res = new BlockListResponse();
        res.setUserId(source.getId());
        res.setBlocks(
                source.getBlocks().values().stream()
                        .filter(b -> !b.getStatus().equals(BlockStatus.REMOVED))
                        .collect(Collectors.toMap(ChatBlock::getBlockedUserId, ChatBlock::getStatus))
        );
        return res;
    }
}
