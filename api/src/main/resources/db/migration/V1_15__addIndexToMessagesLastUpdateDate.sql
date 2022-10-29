drop index if exists  messages_last_update_date_index;
create index messages_last_update_date_index
    on chat.messages (last_update_date);