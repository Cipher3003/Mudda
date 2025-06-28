package com.mudda.backend.postgres.services;

import java.util.List;
import java.util.Optional;

import com.mudda.backend.postgres.models.Category;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService {
    Category createCategory(Category category);
    Optional<Category> findCategoryById(Long id);
    List<Category> findAllCategories();
    void deleteCategory(Long id);
    Optional<Category> findByName(String name);
}

