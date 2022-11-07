package com.andrey.db_entities.chat_channel;

import com.andrey.db_entities.Interactable;
import com.andrey.db_entities.ModificationDateUpdater;
import com.andrey.db_entities.chat_channel_membership.ChannelMembershipRole;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import com.andrey.db_entities.chat_message.ChatMessage;
import com.andrey.db_entities.chat_user.ChatUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "channels", schema = "chat")
public class ChatChannel implements ModificationDateUpdater, Interactable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "channel_name")
    private String channelName;

    @Column(name = "channel_type")
    @Enumerated(EnumType.STRING)
    private ChannelType channelType;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnoreProperties({"ownedChannels"})
    @ToString.Exclude
    private ChatUser owner;

    /*@Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lastUpdateDate", column = @Column(name = "last_update_date")),
            @AttributeOverride(name = "lastUpdateMessageID", column = @Column(name = "last_update_message_id"))
    })
    private ChannelLastUpdateInfo lastUpdateInfo;*/
    @CreationTimestamp
    @Column(name = "last_update_date")
    private Timestamp lastUpdateDate;

    @Column(name = "last_update_message_id")
    private Long lastUpdateMessageID;

    @CreationTimestamp
    @Column(name = "creation_date")
    private Timestamp creationDate;

    @CreationTimestamp
    @Column(name = "modification_date")
    private Timestamp modificationDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ChannelStatus status;

    @Column(name = "status_reason")
    private String statusReason;

    @Column(name = "default_role")
    @Enumerated(EnumType.STRING)
    private ChannelMembershipRole defaultRole;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"channel"})
    @ToString.Exclude
    private Set<ChatChannelMembership> members;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"channel"})
    @ToString.Exclude
    private Set<ChatMessage> messages;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ChatChannel that = (ChatChannel) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @PreUpdate
    @Override
    public void updateModificationDate() {
        Timestamp now = new Timestamp(new Date().getTime());
        Timestamp then = this.getModificationDate();
        if (then.after(now))
            return;
        this.setModificationDate(now);
    }
    @Override
    public boolean isInteractable() {
        return !status.equals(ChannelStatus.REMOVED);
    }
}
