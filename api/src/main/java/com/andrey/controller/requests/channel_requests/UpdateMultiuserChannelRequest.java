package com.andrey.controller.requests.channel_requests;

import com.andrey.Constants;
import com.andrey.db_entities.chat_channel_membership.ChannelMembershipRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMultiuserChannelRequest {

    @Positive
    private long channelId;

    @NotBlank
    @NotNull
    @Size(min = Constants.MIN_MULTIUSER_CHANNEL_NAME_LENGTH, max = Constants.MAX_MULTIUSER_CHANNEL_NAME_LENGTH)
    private String channelName;


    @NotNull
    ChannelMembershipRole defaultRole;
}
