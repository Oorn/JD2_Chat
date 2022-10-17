alter table chat.channels
    add "last_update_message_ID" bigint;

alter table chat.channels
    add constraint channels_messages_id_fk
        foreign key ("last_update_message_ID") references chat.messages;
