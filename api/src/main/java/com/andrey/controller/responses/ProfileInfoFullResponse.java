package com.andrey.controller.responses;

import com.andrey.Constants;
import com.andrey.db_entities.chat_profile.ProfileVisibilityMatchmaking;
import com.andrey.db_entities.chat_profile.ProfileVisibilityUserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProfileInfoFullResponse {
    private Long profileId;

    private String profileName;

    private String profileDescription;

    private int formatVersion;

    private ProfileVisibilityUserInfo visibilityUserInfo;

    private ProfileVisibilityMatchmaking visibilityMatchmaking;
}
