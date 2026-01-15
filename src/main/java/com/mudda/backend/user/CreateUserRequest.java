package com.mudda.backend.user;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateUserRequest(
        @NotBlank String username,
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotNull @Past LocalDate dateOfBirth,
        @NotBlank
        @Pattern(regexp = "^\\+?[0-9]{10,15}$",
                message = "Phone number must be 10-15 digits and can start with +")
        String phoneNumber,
        @NotBlank @Size(min = 8, max = 64) String password,
        @NotNull MuddaUserRole role,
        @Size(max = 512) String profileImageUrl
) {
}
