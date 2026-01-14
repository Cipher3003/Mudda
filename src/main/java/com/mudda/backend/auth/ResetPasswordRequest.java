/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : ResetPasswordRequest
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.auth;

public record ResetPasswordRequest(
        String token,
        String password
) {
}
