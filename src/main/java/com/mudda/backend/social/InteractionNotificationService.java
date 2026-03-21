package com.mudda.backend.social;

import com.mudda.backend.notification.NotificationService;
import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Handles sending FCM notifications for social interactions: mentions, replies, and follows.
 */
@Slf4j
@Service
public class InteractionNotificationService {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public InteractionNotificationService(UserRepository userRepository,
                                          NotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    /**
     * Notify users when they are mentioned in an issue or comment.
     */
    public void notifyMentions(String text, String authorName, String sourceId, String sourceType) {
        Set<String> mentionedUsernames = MentionParser.extractUsernames(text);
        if (mentionedUsernames.isEmpty()) return;

        List<MuddaUser> mentionedUsers = userRepository.findByUsernameIn(mentionedUsernames);
        
        for (MuddaUser user : mentionedUsers) {
            // Don't notify the author if they mentioned themselves
            if (user.getUsername().equals(authorName)) continue;

            sendToUser(user.getUserId(),
                    sourceId,
                    "You were mentioned!",
                    "@" + authorName + " mentioned you in a " + sourceType + ".");
        }
    }

    /**
     * Notify a user when someone replies to their issue or comment.
     */
    public void notifyReply(Long targetUserId, String replierName, String targetId) {
        sendToUser(targetUserId,
                targetId,
                "New Reply",
                replierName + " replied to your Mudda.");
    }

    /**
     * Notify a user when someone follows them.
     */
    public void notifyNewFollower(Long targetUserId, String followerName) {
        sendToUser(targetUserId,
                targetUserId.toString(),
                "New Follower",
                "@" + followerName + " started following you.");
    }

    // ---------- Internal ----------

    private void sendToUser(Long userId, String dataId, String title, String body) {
        if (userId == null) return;
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
