/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : AuthRequest
 * Author  : Vikas Kumar
 * Created : 12-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank
        String username,

        @NotBlank
        @Size(min = 8, max = 64)
        String password
) {
}
