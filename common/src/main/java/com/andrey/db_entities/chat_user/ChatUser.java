package com.andrey.db_entities.chat_user;

import com.andrey.db_entities.Interactable;
import com.andrey.db_entities.ModificationDateUpdater;
import com.andrey.db_entities.chat_block.ChatBlock;
import com.andrey.db_entities.chat_channel.ChatChannel;
import com.andrey.db_entities.chat_channel_invite.ChatChannelInvite;
import com.andrey.db_entities.chat_channel_membership.ChatChannelMembership;
import com.andrey.db_entities.chat_friend_request.ChatFriendRequest;
import com.andrey.db_entities.chat_friendship.ChatFriendship;
import com.andrey.db_entities.chat_profile.ChatProfile;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users", schema = "chat")
public class ChatUser implements ModificationDateUpdater, Interactable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String userName;

    /*
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "email", column = @Column(name = "email")),
            @AttributeOverride(name = "passwordHash", column = @Column(name = "password_hash"))
    })
    private UserCredentials credentials;*/
    //not used because Spring Data doesn't see fields of embedded objects :(

    @Column(name = "email")
    private String email;

    @JsonIgnore
    @Column(name = "password_hash")
    private String passwordHash;

    //TODO more not working UUID experiments?
    /*@Column(name = "uuid", unique = true, insertable = false, updatable = false, nullable = false)
    //@GeneratedValue(generator="system-uuid")
    //@GenericGenerator(name="system-uuid", strategy = "uuid")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type = "uuid-char")
    private String uuid;*/
    /*@GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "uuid", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    @Type(type = "uuid-char")
    private UUID uuid;*/

    @Column(name = "uuid")
    private String uuid;

    @CreationTimestamp
    @Column(name = "last_update_date")
    private Timestamp lastUpdateDate;

    @CreationTimestamp
    @Column(name = "creation_date")
    private Timestamp creationDate;

    @CreationTimestamp
    @Column(name = "modification_date")
    private Timestamp modificationDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "status_reason")
    private String statusReason;

    @Column(name = "email_confirmation_token")
    private String emailConfirmationToken;

    @Column(name = "email_confirmation_token_expiration_date")
    private Timestamp emailConfirmationTokenExpires;

    @Column(name = "password_reset_token")
    private String passwordResetToken;

    @Column(name = "password_reset_token_expiration_date")
    private Timestamp passwordResetTokenExpires;

    @CreationTimestamp
    @Column(name = "password_reset_date")
    private Timestamp passwordResetDate;

    @Column(name = "service_role")
    @Enumerated(EnumType.STRING)
    private UserServiceRole userServiceRole;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"owner"})
    @ToString.Exclude
    private Set<ChatProfile> ownedProfiles;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"owner"})
    @ToString.Exclude
    private Set<ChatChannel> ownedChannels;

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @MapKey(name = "channelId")
    @JsonIgnoreProperties({"user"})
    @ToString.Exclude
    private Map<Long, ChatChannelMembership> channelMemberships;

    @OneToMany(mappedBy = "blockingUser", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @MapKey(name = "blockedUserId")
    @JsonIgnoreProperties({"blockingUser"})
    @ToString.Exclude
    private Map<Long, ChatBlock> blocks;

    @OneToMany(mappedBy = "userWithLesserID", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @MapKey(name = "userIDWithGreaterID")
    @JsonIgnoreProperties({"userWithLesserID"})
    @ToString.Exclude
    private Map<Long, ChatFriendship> friendshipsWithLesserID;

    @OneToMany(mappedBy = "userWithGreaterID", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @MapKey(name = "userIDWithLesserID")
    @JsonIgnoreProperties({"userWithGreaterID"})
    @ToString.Exclude
    private Map<Long, ChatFriendship> friendshipsWithGreaterID;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"recipient"})
    @ToString.Exclude
    private Set<ChatFriendRequest> receivedFriendRequests;

    @OneToMany(mappedBy = "targetUser", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"targetUser"})
    @ToString.Exclude
    private Set<ChatChannelInvite> receivedChannelInvites;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ChatUser chatUser = (ChatUser) o;
        return id != null && Objects.equals(id, chatUser.id);
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
        return !(status.equals(UserStatus.REMOVED)
            || status.equals(UserStatus.DEFAULT_STATUS)
            || status.equals(UserStatus.REQUIRES_EMAIL_CONFIRMATION)
            || status.equals(UserStatus.EMAIL_RECLAIMED));
    }
}
