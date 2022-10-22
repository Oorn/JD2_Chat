alter table chat.users
    add email_confirmation_token varchar(100);

alter table chat.users
    add email_confirmation_token_expiration_date timestamp;

alter table chat.users
    add password_reset_token varchar(100);

alter table chat.users
    add password_reset_token_expiration_date timestamp;

alter table chat.users
    add password_reset_date timestamp;