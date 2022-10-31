package com.andrey.controller.responses;

import com.andrey.db_entities.chat_channel.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChannelInfoResponse {
    private Long channelId;

    private ChannelType channelType;

    private List<UserInfoShortResponse> members;

    //optional
    private UserInfoShortResponse channelOwner;

    //optional
    private String channelName;

    //optional
    private List<ProfileInfoPartialResponse> channelProfiles;


}
