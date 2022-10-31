package com.andrey.controller.responses;

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
public class UserMembershipsWithLastUpdateResponse {
    Long userId;

    List<MembershipWithLastUpdateResponse> channelResponses;
}
