/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : NotificationService
 * Author  : Vikas Kumar
 * Created : 06-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.notification;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    public void sendNotification(String token, String id, String title, String body) {
        Message message = Message.builder()
                .setToken(token)
                .putData("id", id)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
            log.info("Notification sent to firebase successfully");
        } catch (FirebaseMessagingException e) {
            if (e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT ||
                    e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                // TODO: Delete this token from your database because it's dead (Logic)
                log.warn("Token is no longer valid, removing from DB (in future version): {}", token);
            }
            log.error("FCM Error", e);
        }
    }
}
