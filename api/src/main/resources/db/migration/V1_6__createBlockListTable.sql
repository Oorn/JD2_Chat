drop table if exists chat.block_list;
create table chat.block_list
(
    ID                   bigserial
        constraint block_list_pk
            primary key,
    "blocking_user_id"   bigint                                      not null
        constraint block_list_users_id_fk
            references chat.users,
    "blocked_user_id"    bigint                                      not null
        constraint block_list_users_id_fk_2
            references chat.users,
    "blocked_profile_id" bigint
        constraint block_list_profiles_id_fk
            references chat.profiles,
    status               varchar(100) default 'DEFAULT_BLOCK_STATUS' not null,
    status_reason        varchar(100),
    creation_date        timestamp    default current_timestamp not null,
    modification_date    timestamp    default current_timestamp not null
);

drop index if exists  block_list_blocking_user_id_index;
create index block_list_blocking_user_id_index
    on chat.block_list ("blocking_user_id");

drop index if exists  block_list_id_uindex;
create unique index block_list_id_uindex
    on chat.block_list (ID);

