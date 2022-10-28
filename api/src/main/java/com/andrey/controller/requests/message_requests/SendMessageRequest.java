package com.andrey.controller.requests.message_requests;

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
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {

    @Positive
    private long channelId;

    @NotBlank
    @NotNull
    @Size(min = Constants.MIN_MESSAGE_LENGTH, max = Constants.MAX_MESSAGE_LENGTH)
    private String messageBody;

    @Min(Constants.MIN_ACCEPTED_MESSAGE_FORMAT)
    @Max(Constants.MAX_ACCEPTED_MESSAGE_FORMAT)
    private int formatVersion;

}
