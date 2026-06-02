/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : MockQueuePublisher
 * Author  : Vikas Kumar
 * Created : 6/3/2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MockQueuePublisher implements QueuePublisher {

    @Override
    public void publish(String queueName, Object payload) {
        log.trace("Publishing event to Queue: {} Payload: {} ", queueName, payload);
    }
}
