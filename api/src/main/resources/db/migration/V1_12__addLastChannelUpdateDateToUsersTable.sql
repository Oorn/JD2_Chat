alter table chat.users
    add last_update_channel_date timestamp default current_timestamp not null;
