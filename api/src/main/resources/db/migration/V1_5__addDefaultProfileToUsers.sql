alter table chat.users
    add "public_profile_ID" bigint;

alter table chat.users
    add constraint users_public_profile_id_fk
        foreign key ("public_profile_ID") references chat.profiles;

