package com.mudda.backend.category;

public class CategoryMapper {
    public static CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}
