drop table if exists chat.profiles;
create table chat.profiles
(
    ID                  bigserial
        constraint profiles_pk
            primary key,
    owner_user_id     bigint                                               not null,
    profile_name        varchar(100)   default 'DEFAULT_PROFILE_NAME'        not null,
    profile_description varchar(10000) default 'DEFAULT_PROFILE_DESCRIPTION' not null,
    format_version      int            default 1                             not null,
    visibility_matchmaking varchar(100) default 'DEFAULT_PROFILE_SEARCH_VISIBILITY' not null,
    visibility_user_info varchar(100)  default 'DEFAULT_PROFILE_USER_INFO_VISIBILITY' not null,
    status              varchar(100)   default 'DEFAULT_PROFILE_STATUS'      not null,
    status_reason       varchar(100),
    creation_date       timestamp      default current_timestamp             not null,
    modification_date   timestamp      default current_timestamp             not null,
    constraint profiles_owner_id_id_fk
        foreign key ("owner_user_id") references chat.users (id)
);

drop index  if exists profiles_id_uindex;
create unique index profiles_id_uindex
    on chat.profiles (ID);

drop index  if exists profiles_owner_user_id_index;
create index profiles_owner_user_id_index
    on chat.profiles ("owner_user_id");

