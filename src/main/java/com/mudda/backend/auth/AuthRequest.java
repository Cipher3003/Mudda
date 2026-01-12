/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : AuthRequest
 * Author  : Vikas Kumar
 * Created : 12-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.auth;

public record AuthRequest(
        String username,
        String password
) {
}
