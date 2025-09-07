package com.mudda.backend.category;

import jakarta.validation.constraints.NotEmpty;

public record CreateCategoryRequest(
        @NotEmpty String name
) {
}
