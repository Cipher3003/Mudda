/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : UserDeviceToken
 * Author  : Vikas Kumar
 * Created : 20-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.token.device;

import com.mudda.backend.user.MuddaUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity(name = "DeviceToken")
@Table(
        name = "device_tokens",
        schema = "civic",
        indexes = {
                @Index(name = "idx_device_tokens_user_id", columnList = "user_id"),
                @Index(name = "idx_device_tokens_device_id", columnList = "device_id")
        }
)
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "device_tokens_seq")
    @SequenceGenerator(name = "device_tokens_seq", sequenceName = "device_tokens_id_seq")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "fcm_token", nullable = false, columnDefinition = "TEXT")
    private String fcmToken;

    @Column(name = "device_id", nullable = false, unique = true)
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false)
    private DevicePlatform platform;

    @Setter
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Setter
    @Column(name = "updated_at")
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private MuddaUser muddaUser;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    protected DeviceToken() {
    }

    public DeviceToken(Long userId, String fcmToken, String deviceId, DevicePlatform platform) {
        if (userId == null || userId <= 0)
            throw new IllegalArgumentException("userId is null or empty");
        if (fcmToken == null || fcmToken.isEmpty())
            throw new IllegalArgumentException("fcmToken is null or empty");
        if (deviceId == null || deviceId.isEmpty())
            throw new IllegalArgumentException("deviceId is null or empty");
        if (platform == null)
            throw new IllegalArgumentException("platform is null or empty");

        this.userId = userId;
        this.fcmToken = fcmToken;
        this.deviceId = deviceId;
        this.platform = platform;
    }

}
