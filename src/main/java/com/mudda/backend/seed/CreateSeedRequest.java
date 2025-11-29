package com.mudda.backend.seed;

import java.util.List;

public record CreateSeedRequest(
        List<CreateSeedRequestItem> seedDTOList
) {
}
