package com.mudda.backend.seed;

import jakarta.validation.constraints.Positive;

public record CreateSeedRequestItem(
        Entity entity,
        @Positive Integer count
) {
}
