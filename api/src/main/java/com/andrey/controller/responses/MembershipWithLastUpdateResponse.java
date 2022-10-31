package com.andrey.controller.responses;

import com.andrey.db_entities.chat_channel.ChannelType;
import com.andrey.db_entities.chat_channel_membership.ChannelMembershipRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MembershipWithLastUpdateResponse {
    private Long channelId;

    private String channelName;

    private Timestamp lastUpdateTimestamp;

    private Long lastUpdateMessageId;

    private ChannelMembershipRole membershipRole;

    private ChannelType channelType;

}
