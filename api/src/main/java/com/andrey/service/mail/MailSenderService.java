package com.andrey.service.mail;

public interface MailSenderService {
    void sendMail(String address, String subject, String message);
}
