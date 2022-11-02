drop index chat.channel_members_user_id_uindex;

create index channel_members_user_id_index
    on chat.channel_members (user_id);

