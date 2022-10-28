package com.andrey.controller.requests.channel_requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Positive;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FetchProfileChannelRequest {

    @Positive
    private long authUserProfileId;

    @Positive
    private long targetProfileId;
}
