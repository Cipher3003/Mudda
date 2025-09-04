package com.mudda.backend.postgres.services;

import java.util.List;
import java.util.Optional;

import com.mudda.backend.postgres.models.IssueCategory;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService {

    List<IssueCategory> findAllCategories();

    Optional<IssueCategory> findCategoryById(Long id);

    Optional<IssueCategory> findCategoryByName(String name);

    IssueCategory createCategory(IssueCategory category);

    void deleteCategory(Long id);

}
