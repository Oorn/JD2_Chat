package com.andrey.db_entities.chat_block;

import com.andrey.db_entities.Interactable;
import com.andrey.db_entities.ModificationDateUpdater;
import com.andrey.db_entities.chat_profile.ChatProfile;
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
@Table(name = "block_list", schema = "chat")
public class ChatBlock implements ModificationDateUpdater, Interactable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "blocking_user_id", nullable = false)
    @JsonIgnoreProperties({"blocks"})
    @ToString.Exclude
    private ChatUser blockingUser;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_user_id", nullable = false)
    @ToString.Exclude
    private ChatUser blockedUser;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_profile_id", nullable = false)
    @ToString.Exclude
    private ChatProfile blockedProfile;

    @CreationTimestamp
    @Column(name = "creation_date")
    private Timestamp creationDate;

    @CreationTimestamp
    @Column(name = "modification_date")
    private Timestamp modificationDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BlockStatus status;

    @Column(name = "status_reason")
    private String statusReason;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ChatBlock chatBlock = (ChatBlock) o;
        return id != null && Objects.equals(id, chatBlock.id);
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
        return now;
    }
    @Override
    public boolean isInteractable() {
        return !status.equals(BlockStatus.REMOVED);
    }
}
