/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : MockNotificationServiceImpl
 * Author  : Vikas Kumar
 * Created : 11-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.notification;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class MockNotificationServiceImpl implements NotificationService {

    private static final AtomicLong counter = new AtomicLong();

    @PostConstruct
    public void init() {
        log.info("MockNotificationServiceImpl bean initialized. Mocking notifications.");
    }

    @Override
    public void sendNotification(String token, String id, String title, String body) {
        long count = counter.incrementAndGet();
        log.debug("{} Notification has been pushed with details - id: {}, title: {}, body: {}", count, id, title, body);
    }
}
