package com.mudda.backend.postgres.services.impl;

import com.mudda.backend.postgres.models.IssueCategory;
import com.mudda.backend.postgres.repositories.CategoryRepository;
import com.mudda.backend.postgres.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<IssueCategory> findAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<IssueCategory> findCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Optional<IssueCategory> findCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public IssueCategory createCategory(IssueCategory category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

}
