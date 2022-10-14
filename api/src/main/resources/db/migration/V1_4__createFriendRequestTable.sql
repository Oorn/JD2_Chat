drop table if exists chat.friend_requests;
create table chat.friend_requests
(
    ID                bigserial
        constraint friend_requests_pk
            primary key,
    "user_ID_sender"  bigint                                            not null,
    "user_ID_receiver" bigint                                           not null,
    "profile_ID_sender" bigint                                          not null,
    status            varchar(100) default 'DEFAULT_FRIEND_REQUEST_STATUS' not null,
    status_reason     varchar(100),
    creation_date     timestamp    default current_timestamp            not null,
    modification_date timestamp    default current_timestamp            not null,
    constraint friend_requests_users_id_sender_id_fk
        foreign key ("user_ID_sender") references chat.users (id),
    constraint friend_requests_users_id_receiver_id_fk
            foreign key ("user_ID_receiver") references chat.users (id),
    constraint friend_requests_profiles_id_sender_id_fk
            foreign key ("profile_ID_sender") references chat.profiles (id)
);

drop index if exists friend_requests_id_uindex;
create unique index friend_requests_id_uindex
    on chat.friend_requests (ID);

drop index if exists  friend_requests_user_id_sender_index;
create index friend_requests_user_id_sender_index
    on chat.friend_requests ("user_ID_sender");

drop index if exists  friend_requests_user_id_receiver_index;
create index friend_requests_user_id_receiver_index
    on chat.friend_requests ("user_ID_receiver");




