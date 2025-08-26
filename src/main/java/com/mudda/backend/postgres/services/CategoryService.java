package com.mudda.backend.postgres.services;

import java.util.List;
import java.util.Optional;

import com.mudda.backend.postgres.models.Category;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService {

    List<Category> findAllCategories();

    Optional<Category> findCategoryById(Long id);

    Optional<Category> findCategoryByName(String name);

    Category createCategory(Category category);

    void deleteCategory(Long id);

}
