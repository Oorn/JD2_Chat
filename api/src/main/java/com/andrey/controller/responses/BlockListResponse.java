package com.andrey.controller.responses;

import com.andrey.db_entities.chat_block.BlockStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BlockListResponse {

    private long userId;

    private Map<Long, BlockStatus> blocks;
}
