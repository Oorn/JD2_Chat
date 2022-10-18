drop table if exists chat.messages;
create table chat.messages
(
    ID                bigserial
        constraint messages_pk
            primary key,
    "channel_id"      bigint                                          not null
        constraint messages_channels_id_fk
            references chat.channels,
    "sender_id"       bigint                                          not null
        constraint messages_users_id_fk
            references chat.users,
    message_body      varchar(10000) default 'DEFAULT_MESSAGE'        not null,
    format_version    int            default 0                        not null,
    status            varchar(100)   default 'DEFAULT_MESSAGE_STATUS' not null,
    status_reason     varchar(100),
    creation_date     timestamp      default current_timestamp        not null,
    modification_date timestamp      default current_timestamp        not null
);

drop index if exists messages_channel_id_creation_date_index;
create index messages_channel_id_creation_date_index
    on chat.messages ("channel_id", creation_date);

drop index if exists messages_channel_id_modification_date_index;
create index messages_channel_id_modification_date_index
    on chat.messages ("channel_id", modification_date);

drop index if exists messages_id_uindex;
create unique index messages_id_uindex
    on chat.messages (ID);

