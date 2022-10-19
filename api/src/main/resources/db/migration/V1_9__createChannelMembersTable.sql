drop table if exists chat.channel_members;
create table chat.channel_members
(
    ID                bigserial
        constraint channel_members_pk
            primary key,
    "user_id"         bigint                                                   not null
        constraint channel_members_users_id_fk
            references chat.users,
    "channel_id"      bigint                                                   not null
        constraint channel_members_channels_id_fk
            references chat.channels,
    profile_id        bigint
        constraint channel_members_profiles_id_fk
            references chat.profiles,
    role              varchar(100) default 'DEFAULT_ROLE'                      not null,
    status            varchar(100) default 'DEFAULT_CHANNEL_MEMBERSHIP_STATUS' not null,
    status_reason     varchar(100),
    creation_date     timestamp    default current_timestamp                   not null,
    modification_date timestamp    default current_timestamp                   not null
);

drop index if exists channel_members_channel_id_index;
create index channel_members_channel_id_index
    on chat.channel_members ("channel_id");

drop index if exists channel_members_id_uindex;
create unique index channel_members_id_uindex
    on chat.channel_members (ID);

drop index if exists channel_members_user_id_uindex;
create unique index channel_members_user_id_uindex
    on chat.channel_members ("user_id");

