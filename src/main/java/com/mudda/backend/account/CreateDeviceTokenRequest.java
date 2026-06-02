/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : CreateDeviceTokenRequest
 * Author  : Vikas Kumar
 * Created : 21-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.account;

import com.mudda.backend.token.device.DevicePlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateDeviceTokenRequest(
        @NotNull @NotBlank String deviceId,
        @NotNull @NotBlank String fcmToken,
        @NotNull DevicePlatform platform
) {
}
