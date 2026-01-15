/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : AuthResult
 * Author  : Vikas Kumar
 * Created : 13-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.auth;

import com.mudda.backend.user.MuddaUser;

public record AuthResult(
        String accessToken,
        String refreshToken,
        long accessExpiresInMs,
        MuddaUser user
) {
}
