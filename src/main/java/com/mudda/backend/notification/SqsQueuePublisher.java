/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : SqsQueuePublisher
 * Author  : Vikas Kumar
 * Created : 6/3/2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.notification;

import io.awspring.cloud.sqs.operations.SendResult;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile("!dev")
public class SqsQueuePublisher implements QueuePublisher {

    private final SqsTemplate sqsTemplate;

    public SqsQueuePublisher(SqsTemplate sqsTemplate) {
        this.sqsTemplate = sqsTemplate;
    }

    @Override
    public void publish(String queueName, Object payload) {
        SendResult<Object> result = sqsTemplate.send(queueName, payload);
        log.trace("Queue: {} Payload: {} Result: {} ", queueName, payload, result);
    }
}
