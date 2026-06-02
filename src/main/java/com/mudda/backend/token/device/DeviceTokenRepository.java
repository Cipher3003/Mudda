/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : UserDeviceTokenRepository
 * Author  : Vikas Kumar
 * Created : 20-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.token.device;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {

    Optional<DeviceToken> findByDeviceId(String deviceId);

    @Query("SELECT d.id, d.fcmToken FROM DeviceToken d WHERE d.userId = :userId AND d.active = TRUE")
    List<DeviceTokenProjection> findDeviceTokenProjectionByUserId(Long userId);

    void deleteDeviceTokenByDeviceId(String deviceId);

    void deleteDeviceTokenByUserId(Long userId);

}
