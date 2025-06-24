package com.mudda.backend.services;

import com.mudda.backend.models.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService {
    Category createCategory(Category category);
    Optional<Category> findCategoryById(Long id);
    List<Category> findAllCategories();
    void deleteCategory(Long id);
}

