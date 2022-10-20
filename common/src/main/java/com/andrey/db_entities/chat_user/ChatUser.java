package com.andrey.db_entities.chat_user;

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
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.cglib.core.Block;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users", schema = "chat")
public class ChatUser implements ModificationDateUpdater {
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

    //@Column(name = "public_profile_ID")
    //private Long publicProfileID;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"owner"})
    @ToString.Exclude
    private Set<ChatProfile> ownedProfiles;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"owner"})
    @ToString.Exclude
    private Set<ChatChannel> ownedChannels;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"user"})
    @ToString.Exclude
    private Set<ChatChannelMembership> channelMemberships;

    @OneToMany(mappedBy = "blockingUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"blockingUser"})
    @ToString.Exclude
    private Set<ChatBlock> blocks;

    @OneToMany(mappedBy = "userWithLesserID", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"userWithLesserID"})
    @ToString.Exclude
    private Set<ChatFriendship> friendshipsWithLesserID;

    @OneToMany(mappedBy = "userWithGreaterID", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"userWithGreaterID"})
    @ToString.Exclude
    private Set<ChatFriendship> friendshipsWithGreaterID;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"recipient"})
    @ToString.Exclude
    private Set<ChatFriendRequest> receivedFriendRequests;

    @OneToMany(mappedBy = "targetUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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

    @Override
    public Timestamp UpdateModificationDate(Timestamp now) {
        Timestamp then = this.getModificationDate();
        if (then.after(now))
            return then;
        this.setModificationDate(now);
        return now;
    }
}