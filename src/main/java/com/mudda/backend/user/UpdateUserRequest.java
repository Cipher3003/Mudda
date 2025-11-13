package com.mudda.backend.user;

/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : UpdateUserRequest
 * Author  : Vikas Kumar
 * Created : 13-11-2025
 * ---------------------------------------------------------------
 */
public record UpdateUserRequest(
        String phoneNumber,
        String profileImageUrl
) {
}
