/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : RefreshRequest
 * Author  : Vikas Kumar
 * Created : 13-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.auth;

public record RefreshRequest(
        String refreshToken
) {
}
