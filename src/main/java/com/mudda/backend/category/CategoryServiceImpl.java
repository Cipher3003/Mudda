package com.mudda.backend.category;

import com.mudda.backend.exceptions.DuplicateEntityException;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.amazonaws.services.appintegrations.model.DuplicateResourceException;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // #region Queries (Read Operations)

    @Override
    public List<CategoryResponse> findAllCategories(@RequestParam String search) {
        if (search == null || search.isBlank())
            return categoryRepository.findAllProjectedBy();
        return categoryRepository.findByNameContainingIgnoreCase(search);
    }

    @Override
    public Optional<CategoryResponse> findById(long id) {
        return categoryRepository.findProjectedById(id);
    }

    // #endregion

    // #region Commands (Write Operation)

    @Transactional
    @Override
    public CategoryResponse createCategory(CreateCategoryRequest categoryRequest) {
        Category saved;

        try {
            saved = categoryRepository.save(new Category(categoryRequest.name()));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEntityException("Category with name %s already exists"
                    .formatted(categoryRequest.name()));
        }

        return CategoryMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public void deleteCategory(long id) {
        categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + id));

        categoryRepository.deleteById(id);
    }

    // #endregion

}
