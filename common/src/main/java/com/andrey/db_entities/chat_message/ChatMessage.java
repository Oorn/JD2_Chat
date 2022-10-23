package com.andrey.db_entities.chat_message;

import com.andrey.db_entities.Interactable;
import com.andrey.db_entities.ModificationDateUpdater;
import com.andrey.db_entities.chat_channel.ChannelLastUpdateInfo;
import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.db_entities.chat_user.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "messages", schema = "chat")
public class ChatMessage implements ModificationDateUpdater, Interactable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    @JsonIgnoreProperties({"messages"})
    @ToString.Exclude
    private ChatChannel channel;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @ToString.Exclude
    private ChatUser sender;

    @Column(name = "message_body")
    private String messageBody;

    @Column(name = "format_version")
    private Integer formatVersion;

    @CreationTimestamp
    @Column(name = "creation_date")
    private Timestamp creationDate;

    @CreationTimestamp
    @Column(name = "modification_date")
    private Timestamp modificationDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    @Column(name = "status_reason")
    private String statusReason;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ChatMessage that = (ChatMessage) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public Timestamp UpdateModificationDate(Timestamp now) {
        Timestamp then = this.getModificationDate();
        if (then.after(now))
            return then;
        this.setModificationDate(now);

        ChannelLastUpdateInfo lastUpdate = getChannel().getLastUpdateInfo();
        //TODO use some kind of compareAndSwap?
        //check if last update is already more recent than now
        if (lastUpdate.getLastUpdateDate().after(now))
            return now;
        if ((lastUpdate.getLastUpdateDate().equals(now))
                && (lastUpdate.getLastUpdateMessageID() > getId()))
            return now;

        //set new last update info, using clone to avoid potentially confusing JPA
        getChannel().setLastUpdateInfo(new ChannelLastUpdateInfo((Timestamp) now.clone(), getId().longValue()));

        return now;
    }
    @Override
    public boolean isInteractable() {
        return !status.equals(MessageStatus.REMOVED);
    }
}
