package com.mudda.backend.seed;

import jakarta.validation.constraints.Positive;

public record SeedDTO(
        Entity entity,
        @Positive Integer count
) {
}
