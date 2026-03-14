/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : AuthMapper
 * Author  : Vikas Kumar
 * Created : 13-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.auth;

import com.mudda.backend.auth.dto.AuthResponse;
import com.mudda.backend.auth.dto.AuthResult;
import com.mudda.backend.auth.dto.AuthenticatedUserResponse;

public class AuthMapper {

    public static AuthResponse toAuthResponse(AuthResult authResult) {
        return new AuthResponse(
                authResult.accessToken(),
                authResult.refreshToken(),
                "Bearer",
                authResult.accessExpiresInMs(),
                new AuthenticatedUserResponse(
                        authResult.user().getUserId(),
                        authResult.user().getUsername(),
                        authResult.user().isEnabled()
                )
        );
    }
}
