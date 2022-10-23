package com.andrey.db_entities.chat_profile;

import com.andrey.db_entities.Interactable;
import com.andrey.db_entities.ModificationDateUpdater;
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
import javax.persistence.OneToOne;
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
@Table(name = "profiles", schema = "chat")
public class ChatProfile implements ModificationDateUpdater, Interactable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_ID", nullable = false)
    @JsonIgnoreProperties({"ownedProfiles"})
    @ToString.Exclude
    private ChatUser owner;

    @Column(name = "profile_name")
    private String profileName;

    @Column(name = "profile_description")
    private String profileDescription;

    @Column(name = "format_version")
    private Integer formatVersion;

    @Column(name = "visibility_matchmaking")
    @Enumerated(EnumType.STRING)
    private ProfileVisibilityMatchmaking profileVisibilityMatchmaking;

    @Column(name = "visibility_user_info")
    @Enumerated(EnumType.STRING)
    private ProfileVisibilityUserInfo profileVisibilityUserInfo;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ProfileStatus status;

    @Column(name = "status_reason")
    private String statusReason;

    @CreationTimestamp
    @Column(name = "creation_date")
    private Timestamp creationDate;

    @CreationTimestamp
    @Column(name = "modification_date")
    private Timestamp modificationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ChatProfile that = (ChatProfile) o;
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
        return now;
    }

    @Override
    public boolean isInteractable() {
        return !status.equals(ProfileStatus.REMOVED);
    }
}
