alter table chat.messages
    add last_update_date timestamp default current_timestamp not null;