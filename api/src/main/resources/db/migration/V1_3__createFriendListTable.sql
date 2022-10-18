drop table if exists chat.friend_list;
create table chat.friend_list
(
    ID                bigserial
        constraint friend_list_pk
            primary key,
    user_id_lesser  bigint                                            not null,
    user_id_greater bigint                                            not null,
    status            varchar(100) default 'DEFAULT_FRIEND_LIST_STATUS' not null,
    status_reason     varchar(100),
    creation_date     timestamp    default current_timestamp            not null,
    modification_date timestamp    default current_timestamp            not null,
    constraint friend_list_users_id_lesser_id_fk
        foreign key ("user_id_lesser") references chat.users (id),
    constraint friend_list_users_id_greater_id_fk
        foreign key ("user_id_greater") references chat.users (id)
);

drop index if exists friend_list_id_uindex;
create unique index friend_list_id_uindex
    on chat.friend_list (ID);

drop index if exists  friend_list_user_id_greater_index;
create index friend_list_user_id_greater_index
    on chat.friend_list ("user_id_greater");

drop index if exists  friend_list_user_id_lesser_index;
create index friend_list_user_id_lesser_index
    on chat.friend_list ("user_id_lesser");

