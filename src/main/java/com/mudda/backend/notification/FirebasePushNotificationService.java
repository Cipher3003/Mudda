/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : NotificationServiceImpl
 * Author  : Vikas Kumar
 * Created : 11-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.notification;

import com.google.firebase.messaging.*;
import com.mudda.backend.token.device.DeviceTokenProjection;
import com.mudda.backend.token.device.DeviceTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Profile({"prod", "stage"})
public class FirebasePushNotificationService implements PushNotificationService {

    private final DeviceTokenService deviceTokenService;

    public FirebasePushNotificationService(DeviceTokenService deviceTokenService) {
        this.deviceTokenService = deviceTokenService;
    }

    @Override
    public void sendNotification(Long userId, String id, String title, String body) {
        List<DeviceTokenProjection> tokenProjections = deviceTokenService.getDeviceTokenProjectionByUserId(userId);

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData("id", id)
                .addAllTokens(tokenProjections.stream().map(DeviceTokenProjection::getFcmToken).toList())
                .build();

        try {
            BatchResponse batchResponse = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            log.info("Notification sent to firebase successfully Success: {}, Failed: {}",
                    batchResponse.getSuccessCount(), batchResponse.getFailureCount()
            );

            List<SendResponse> responses = batchResponse.getResponses();
            List<Long> failedTokenIds = new ArrayList<>(tokenProjections.size());

            for (int i = 0; i < responses.size(); i++)
                if (!responses.get(i).isSuccessful())
                    failedTokenIds.add(tokenProjections.get(i).getId());

            deviceTokenService.deleteByFcmTokensIn(failedTokenIds);

        } catch (FirebaseMessagingException e) {
            log.error("FCM Error", e);
        }
    }

}
