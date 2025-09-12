package com.mudda.backend.user;

import java.time.LocalDate;

public record CreateUserRequest(
        String userName,
        String name,
        String email,
        LocalDate dateOfBirth,
        String phoneNumber,
        String hashedPassword,
        Long roleId,
        String profileImageUrl
) {
}
