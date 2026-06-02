/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : DeviceTokenProjection
 * Author  : Vikas Kumar
 * Created : 21-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.token.device;

public interface DeviceTokenProjection {

    Long getId();

    String getFcmToken();
}
