package com.andrey.db_entities.chat_channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Embeddable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
@Deprecated //CreationTimestamp in Embeddable hasn't been working in hibernate for 4+ years, WTF
public class ChannelLastUpdateInfo {

    private Timestamp lastUpdateDate;


    private Long lastUpdateMessageID;
}
