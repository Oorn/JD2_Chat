drop table if exists chat.users;
create table chat.users
(
    ID                bigserial
        constraint users_pk
            primary key,
    username          varchar(100) default 'DEFAULT_USERNAME' not null,
    email             varchar(100)                            not null,
    password_hash     varchar(100) default 'NO_PASSWORD'      not null,
    UUID              varchar(32),
    last_update_date  timestamp    default CURRENT_TIMESTAMP  not null,
    creation_date     timestamp    default CURRENT_TIMESTAMP  not null,
    modification_date timestamp    default CURRENT_TIMESTAMP  not null,
    status            varchar(100) default 'DEFAULT_STATUS'   not null,
    status_reason     varchar(100)
);

drop index  if exists users_email_uindex;
create unique index users_email_uindex
    on chat.users (email);

drop index  if exists users_id_uindex;
create unique index users_id_uindex
    on chat.users (ID);

drop index  if exists users_uuid_uindex;
create unique index users_uuid_uindex
    on chat.users (UUID);

