package com.mudda.backend.role;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateRoleRequest(
        @NotNull @NotEmpty String name
) {
}
