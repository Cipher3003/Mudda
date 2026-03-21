/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : IssueEventListener
 * Author  : Vikas Kumar
 * Created : 06-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.issue;

import com.mudda.backend.issue.dto.IssueFlagResponse;
import com.mudda.backend.notification.PushNotificationService;
import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.UserRepository;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IssueEventListeners {

    private final IssueService issueService;
    private final UserRepository userRepository;
    private final PushNotificationService pushNotificationService;

    private final String RESPONSE_QUEUE_NAME = "mudda-hate-speech-response-queue";

    public IssueEventListeners(
            IssueService issueService,
            UserRepository userRepository,
            PushNotificationService pushNotificationService
    ) {
        this.issueService = issueService;
        this.userRepository = userRepository;
        this.pushNotificationService = pushNotificationService;
    }

    @SqsListener(value = RESPONSE_QUEUE_NAME, pollTimeoutSeconds = "20")
    public void onIssueFlagResponse(IssueFlagResponse response) {
        log.info("Received response from SQS: {}", response);

        if (response.hate()) {
            issueService.softDelete(response.id());
            log.info("Sending notification for Issue: {} marked for hate", response.id());

            userRepository.findById(response.userId())
                    .ifPresent(user -> pushNotificationService.sendNotification(
                            user.getUserId(), Long.toString(response.id()), "Issue removed",
                            "Your issue has been removed for foul language."
                    ));
        } else if (response.isOld()) {
            issueService.restoreIssue(response.id());
            log.info("Sending notification for Old Issue: {} good to go", response.id());

            userRepository.findById(response.userId())
                    .ifPresent(user -> pushNotificationService.sendNotification(
                            user.getUserId(), Long.toString(response.id()), "Issue removed again",
                            "Your issue has been removed again for foul language."
                    ));
        }
    }
}
