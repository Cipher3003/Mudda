package com.mudda.backend.category;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(@NotBlank String name) {
}
