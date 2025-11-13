package com.mudda.backend.category;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public interface CategoryService {

    List<CategoryResponse> findAllCategories(String name);

    Optional<CategoryResponse> findById(long id);

    CategoryResponse createCategory(CreateCategoryRequest categoryRequest);

    void deleteCategory(long id);

}
