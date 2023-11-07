package com.andrey.db_entities.chat_friend_request;

import com.andrey.db_entities.Interactable;
import com.andrey.db_entities.ModificationDateUpdater;
import com.andrey.db_entities.chat_profile.ChatProfile;
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
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "friend_requests", schema = "chat")
public class ChatFriendRequest implements ModificationDateUpdater, Interactable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_sender", nullable = false)
    @ToString.Exclude
    private ChatUser sender;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_recipient", nullable = false)
    @JsonIgnoreProperties({"receivedFriendRequests"})
    @ToString.Exclude
    private ChatUser recipient;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id_sender", nullable = false)
    @JsonIgnoreProperties({"owner"})
    @ToString.Exclude
    private ChatProfile senderProfile;

    @CreationTimestamp
    @Column(name = "creation_date")
    private Timestamp creationDate;

    @CreationTimestamp
    @Column(name = "modification_date")
    private Timestamp modificationDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private FriendRequestStatus status;

    @Column(name = "status_reason")
    private String statusReason;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ChatFriendRequest that = (ChatFriendRequest) o;
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
        return status.equals(FriendRequestStatus.PENDING);
    }
}
