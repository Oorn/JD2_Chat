package com.andrey.controller.responses;

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
public class PendingInviteResponse {
    private long inviteId;

    private UserInfoShortResponse sender;

    private String channelName;
}
