package com.andrey.stomp;

import com.andrey.service.MessageUpdateDatePropagateService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Service
public class TestMessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final MessageUpdateDatePropagateService messageUpdateDatePropagateService;

    @PostConstruct
    private void postConstruct() {
        messageUpdateDatePropagateService.setUserNotificationService(this::pingUser);
    }

    public void sendToUser (String targetUser, Object message) {
        simpMessagingTemplate.convertAndSendToUser(targetUser, "/queue/messages", message);
    }

    public void pingUser(String targetUser) {
        sendToUser(targetUser, "ping");
    }
}
