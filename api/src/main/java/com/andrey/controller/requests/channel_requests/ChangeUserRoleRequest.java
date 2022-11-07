package com.andrey.controller.requests.channel_requests;

import com.andrey.db_entities.chat_channel_membership.ChannelMembershipRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChangeUserRoleRequest {

    @Positive
    private long channelId;

    @Positive
    private long userId;

    @NotNull
    ChannelMembershipRole newRole;

}
