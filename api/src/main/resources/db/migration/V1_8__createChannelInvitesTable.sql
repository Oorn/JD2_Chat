create table chat.channel_invites
(
    ID                bigserial
        constraint channel_invites_pk
            primary key,
    "sender_ID"       bigint                                       not null
        constraint channel_invites_users_id_fk
            references chat.users,
    "channel_ID"      bigint                                       not null
        constraint channel_invites_channel_id_fk
            references chat.channels,
    "invite_UUID"     varchar(32),
    invite_type       varchar(100) default 'DEFAULT_INVITE_TYPE'   not null,
    "target_user_ID"  bigint
        constraint channel_invites_users_id_fk_2
            references chat.users,
    max_uses          int          default 1                       not null,
    times_used        int          default 0                       not null,
    expiration_date   timestamp    default current_timestamp       not null,
    status            varchar(100) default 'DEFAULT_INVITE_STATUS' not null,
    status_reason     varchar(100),
    creation_date     timestamp    default current_timestamp       not null,
    modification_date timestamp    default current_timestamp       not null
);

create unique index channel_invites_id_uindex
    on chat.channel_invites (ID);

create index channel_invites_invite_uuid_index
    on chat.channel_invites ("invite_UUID");

create index channel_invites_target_user_id_index
    on chat.channel_invites ("target_user_ID");

