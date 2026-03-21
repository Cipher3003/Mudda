/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : DeviceTokenService
 * Author  : Vikas Kumar
 * Created : 21-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.token.device;

import com.mudda.backend.account.CreateDeviceTokenRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;

    public DeviceTokenService(DeviceTokenRepository deviceTokenRepository) {
        this.deviceTokenRepository = deviceTokenRepository;
    }

    public List<DeviceTokenProjection> getDeviceTokenProjectionByUserId(Long userId) {
        return deviceTokenRepository.findDeviceTokenProjectionByUserId(userId);
    }

    @Transactional
    public void registerToken(CreateDeviceTokenRequest tokenRequest, Long userId) {
        Optional<DeviceToken> existingDeviceToken = deviceTokenRepository.findByDeviceId(tokenRequest.deviceId());
        DeviceToken deviceToken;

        if (existingDeviceToken.isPresent()) {
            deviceToken = existingDeviceToken.get();
            deviceToken.setFcmToken(tokenRequest.fcmToken());
        } else
            deviceToken = new DeviceToken(
                    userId,
                    tokenRequest.fcmToken(),
                    tokenRequest.deviceId(),
                    tokenRequest.platform()
            );

        deviceTokenRepository.save(deviceToken);
    }

    @Transactional
    public void unregisterDevice(String deviceId) {
        deviceTokenRepository.deleteDeviceTokenByDeviceId(deviceId);
    }

    @Transactional
    public void deleteUserTokens(Long userId) {
        deviceTokenRepository.deleteDeviceTokenByUserId(userId);
    }

    @Transactional
    public void deleteByFcmTokensIn(List<Long> failedTokensIds) {
        deviceTokenRepository.deleteAllById(failedTokensIds);
    }
}
