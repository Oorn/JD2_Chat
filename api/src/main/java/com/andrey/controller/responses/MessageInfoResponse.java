package com.andrey.controller.responses;

import com.andrey.Constants;
import com.andrey.db_entities.chat_message.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;


@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageInfoResponse {

    private long messageId;

    private String messageBody;

    private int formatVersion;

    private MessageStatus status;

    private Timestamp lastUpdateDate;

    private UserInfoShortResponse sender;
}
