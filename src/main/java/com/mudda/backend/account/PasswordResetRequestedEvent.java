/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : PasswordResetRequestedEvent
 * Author  : Vikas Kumar
 * Created : 7/10/2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.account;

public record PasswordResetRequestedEvent(
        String email,
        String token
) {
}
