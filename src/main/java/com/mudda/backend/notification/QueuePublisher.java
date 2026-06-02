/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : QueuePublisher
 * Author  : Vikas Kumar
 * Created : 6/3/2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.notification;

public interface QueuePublisher {
    void publish(String queueName, Object payload);
}
