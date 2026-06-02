package com.mudda.backend.seed;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateSeedRequest(
        @NotNull @Size(min = 1, max = 4) List<CreateSeedRequestItem> seedDTOList
) {
}
