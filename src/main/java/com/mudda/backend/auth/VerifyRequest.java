/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : VerifyRequest
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerifyRequest(
        @NotBlank @Email String email
) {
}
