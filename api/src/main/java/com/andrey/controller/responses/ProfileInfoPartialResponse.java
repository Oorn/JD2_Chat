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
public class ProfileInfoPartialResponse {
    private Long profileId;

    private String profileName;

    private String profileDescription;

    private int formatVersion;

}
