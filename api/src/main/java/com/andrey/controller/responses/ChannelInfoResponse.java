package com.andrey.controller.responses;

import com.andrey.db_entities.chat_channel.ChannelType;
import com.andrey.db_entities.chat_channel_membership.ChannelMembershipRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChannelInfoResponse {
    private Long channelId;

    private ChannelType channelType;

    private List<ChannelMemberInfoResponse> members;

    //optional
    private UserInfoShortResponse channelOwner;

    //optional
    private ChannelMembershipRole defaultRole;

    //optional
    private String channelName;

    //optional
    private Map<Long, ProfileInfoPartialResponse> channelProfiles;


}
