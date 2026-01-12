/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : AuthResponse
 * Author  : Vikas Kumar
 * Created : 12-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.auth;

public record AuthResponse(
        String accessToken,
        String tokenType,
        Long expiresIn,
        AuthenticatedUserResponse user
) {
}
