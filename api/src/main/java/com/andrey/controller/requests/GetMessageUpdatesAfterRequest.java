package com.andrey.controller.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.sql.Timestamp;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GetMessageUpdatesAfterRequest {

    @Positive
    long channelId;

    @Positive
    long afterMessageId;

    @NotNull
    Timestamp afterMessageTimestamp;
}
