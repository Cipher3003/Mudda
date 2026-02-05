/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : AuthenticatedUserResponse
 * Author  : Vikas Kumar
 * Created : 12-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.auth;

public record AuthenticatedUserResponse(
        Long id,
        String username,
        Boolean verified
) {
}
