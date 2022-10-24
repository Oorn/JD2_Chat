package com.andrey.controller.requests.profile_requests;

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
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateProfileRequest {

    @NotBlank
    @NotNull
    @Size(min = Constants.MIN_PROFILE_NAME_LENGTH, max = Constants.MAX_PROFILE_NAME_LENGTH)
    private String profileName;

    @NotBlank
    @NotNull
    @Size(min = Constants.MIN_PROFILE_DESCRIPTION_LENGTH, max = Constants.MAX_PROFILE_DESCRIPTION_LENGTH)
    private String profileDescription;

    @Min(Constants.MIN_ACCEPTED_PROFILE_FORMAT)
    @Max(Constants.MAX_ACCEPTED_PROFILE_FORMAT)
    private int formatVersion;

    @NotNull
    private ProfileVisibilityUserInfo visibilityUserInfo;

    @NotNull
    private ProfileVisibilityMatchmaking visibilityMatchmaking;
}
