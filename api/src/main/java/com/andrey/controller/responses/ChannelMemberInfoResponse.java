package com.andrey.controller.responses;

import com.andrey.db_entities.chat_channel_membership.ChannelMembershipRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChannelMemberInfoResponse {
    private UserInfoShortResponse user;
    private ChannelMembershipRole role;
}
