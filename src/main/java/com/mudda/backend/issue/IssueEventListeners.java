/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : IssueEventListener
 * Author  : Vikas Kumar
 * Created : 06-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.issue;

import com.mudda.backend.notification.NotificationService;
import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.UserRepository;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class IssueEventListeners {

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private final String RESPONSE_QUEUE_NAME = "mudda-hate-speech-response-queue";

    public IssueEventListeners(IssueRepository issueRepository,
                               UserRepository userRepository,
                               NotificationService notificationService) {
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @SqsListener(value = RESPONSE_QUEUE_NAME, pollTimeoutSeconds = "20")
    public void onIssueFlagResponse(IssueFlagResponse response) {
        log.info("Received response from SQS: {}", response);
        if (response.hate()) {
            issueRepository.findById(response.id()).ifPresent(issue -> {
                issue.setDeleteFlag(true);
                issueRepository.save(issue);
                log.info("Sending notification for Issue: {} marked for hate", response.id());
                Optional<String> token = userRepository.findById(response.userId())
                        .map(MuddaUser::getFcmToken);

                token.ifPresent(s -> notificationService.sendNotification(
                        s, Long.toString(response.id()), "Issue removed",
                        "Your issue has been removed for foul language."
                ));
            });
        } else if (response.isOld()) {
            issueRepository.findById(response.id()).ifPresent(issue -> {
                issue.setDeleteFlag(false);
                issueRepository.save(issue);
                log.info("Sending notification for Old Issue: {} good to go", response.id());
                Optional<String> token = userRepository.findById(response.userId())
                        .map(MuddaUser::getFcmToken);

                token.ifPresent(s -> notificationService.sendNotification(
                        s, Long.toString(response.id()), "Issue removed again",
                        "Your issue has been removed again for foul language."
                ));
            });
        }
    }
}
