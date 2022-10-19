package com.andrey.db_entities.chat_channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class ChannelLastUpdateInfo {
    private Timestamp lastUpdateDate;

    private Long lastUpdateMessageID;
}
