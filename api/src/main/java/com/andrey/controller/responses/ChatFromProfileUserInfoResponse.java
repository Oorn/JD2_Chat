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
@Deprecated
public class ChatFromProfileUserInfoResponse {
    private ProfileInfoPartialResponse profile;

    private UserInfoShortResponse user;
}