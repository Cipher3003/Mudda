package com.mudda.backend.user;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateUserRequest(
        @NotBlank String userName,
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotNull @Past LocalDate dateOfBirth,
        @NotBlank
        @Pattern(regexp = "^\\+?[0-9]{10,15}$",
                message = "Phone number must be 10-15 digits and can start with +")
        String phoneNumber,
        @NotBlank String password,
        @NotNull MuddaUserRole role,
        String profileImageUrl
) {
}
