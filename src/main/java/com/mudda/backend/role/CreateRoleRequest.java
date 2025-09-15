package com.mudda.backend.role;

import jakarta.validation.constraints.NotBlank;

public record CreateRoleRequest(
        @NotBlank String name
) {
}
