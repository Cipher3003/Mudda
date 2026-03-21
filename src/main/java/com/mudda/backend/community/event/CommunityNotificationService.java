package com.mudda.backend.community.event;

import com.mudda.backend.notification.NotificationService;
import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Centralized service for community-specific FCM push notification triggers.
 * Called directly from services (not via events) for fine-grained control.
 */
@Slf4j
@Service
public class CommunityNotificationService {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public CommunityNotificationService(UserRepository userRepository,
                                        NotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    /**
     * Notify an issue reporter that their issue has been resolved by a community admin.
     */
    public void notifyIssueResolved(Long reporterUserId, Long issueId, String issueTitle) {
        sendToUser(reporterUserId,
                String.valueOf(issueId),
                "✅ Issue Resolved",
                "Your issue \"" + issueTitle + "\" has been resolved.");
    }

    /**
     * Notify a user that their community membership request was accepted.
     */
    public void notifyMemberVerified(Long userId, String communityName) {
        sendToUser(userId,
                communityName,
                "🎉 Welcome!",
                "You've been verified as a resident of " + communityName + ".");
    }

    /**
     * Notify a user that their community membership request was rejected.
     */
    public void notifyMemberRejected(Long userId, String communityName) {
        sendToUser(userId,
                communityName,
                "Membership Update",
                "Your request to join " + communityName + " was not approved.");
    }

    // ---------- Internal ----------

    private void sendToUser(Long userId, String dataId, String title, String body) {
        userRepository.findById(userId).ifPresent(user -> {
            String token = user.getFcmToken();
            if (token != null && !token.isBlank()) {
                try {
                    notificationService.sendNotification(token, dataId, title, body);
                    log.debug("Sent FCM '{}' to user {}", title, userId);
                } catch (Exception e) {
                    log.warn("Failed to send FCM to user {}: {}", userId, e.getMessage());
                }
            }
        });
    }
}
