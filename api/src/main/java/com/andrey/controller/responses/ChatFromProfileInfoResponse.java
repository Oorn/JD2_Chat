package com.andrey.controller.responses;

import com.andrey.db_entities.chat_channel.ChannelLastUpdateInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatFromProfileInfoResponse {

    private Long channelId;

    //private ChannelLastUpdateInfo lastUpdateInfo;

    private Timestamp lastUpdateDate;

    private Long lastUpdateMessageId;

    private List<ChatFromProfileUserInfoResponse> users;
}
