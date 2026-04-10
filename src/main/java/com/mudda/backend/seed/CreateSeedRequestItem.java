package com.mudda.backend.seed;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateSeedRequestItem(
        @NotNull Entity entity,
        @Positive Integer count
) {
}
