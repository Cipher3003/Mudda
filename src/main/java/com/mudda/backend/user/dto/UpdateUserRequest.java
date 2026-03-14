/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : UpdateUserRequest
 * Author  : Vikas Kumar
 * Created : 13-11-2025
 * ---------------------------------------------------------------
 */
package com.mudda.backend.user.dto;

public record UpdateUserRequest(
        String phoneNumber,
        String profileImageUrl
) {
}
