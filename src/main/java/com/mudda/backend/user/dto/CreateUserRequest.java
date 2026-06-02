package com.mudda.backend.user.dto;

import com.mudda.backend.user.MuddaUserRole;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateUserRequest(
        @NotBlank
        @Size(min = 3, max = 20)
        @Pattern(regexp = "^(?![._])[a-zA-Z0-9._]{3,20}(?<![._])$",
                message = "Username must be alphabets, numbers or '_', '.'")
        String username,

        @NotBlank @Size(min = 1, max = 100) String name,

        @NotBlank @Size(max = 254) @Email String email,

        @NotNull @Past LocalDate dateOfBirth,

        @NotBlank
        @Pattern(regexp = "^\\+?[0-9]{10,15}$",
                message = "Phone number must be 10-15 digits and can start with +")
        String phoneNumber,

        @NotBlank @Size(min = 8, max = 64) String password,

        @NotBlank MuddaUserRole role,

        @Size(max = 512) String profileImageKey
) {
}
