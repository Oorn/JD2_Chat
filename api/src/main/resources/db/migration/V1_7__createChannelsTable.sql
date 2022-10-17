drop table if exists chat.channels;
create table chat.channels
(
    ID                bigserial
        constraint channels_pk
            primary key,
    channel_name      varchar(100) default 'DEFAULT_CHANNEL_NAME'   not null,
    channel_type      varchar(100) default 'DEFAULT_CHANNEL_TYPE'   not null,
    "owner_ID"        bigint                                        not null
        constraint channels_users_id_fk
            references chat.users,
    last_update_date  timestamp    default CURRENT_TIMESTAMP  not null,
    status            varchar(100) default 'DEFAULT_CHANNEL_STATUS' not null,
    status_reason     varchar(100),
    creation_date     timestamp    default current_timestamp        not null,
    modification_date timestamp    default current_timestamp        not null
);

drop index if exists channels_id_uindex;
create unique index channels_id_uindex
    on chat.channels (ID);

drop index if exists channels_owner_id_index;
create index channels_owner_id_index
    on chat.channels ("owner_ID");

