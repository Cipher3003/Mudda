package com.mudda.backend.category;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

//    ------------------------------
//     Read Operations (queries)
//    ------------------------------

    @Override
    public List<CategoryResponse> findAllCategories(@RequestParam String search) {
        if (search == null) return categoryRepository.findAllProjectedBy();
        return categoryRepository.findByNameContainingIgnoreCase(search);
    }

    @Override
    public Optional<CategoryResponse> findById(Long id) {
        return categoryRepository.findProjectedById(id);
    }

//    ------------------------------
//    Write Operations (commands)
//    ------------------------------

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest categoryRequest) {
        String name = categoryRequest.name();
        if (categoryRepository.findByName(name).isPresent())
            throw new IllegalArgumentException("Category with name " + name + " already exists.");

        Category category = new Category(name);
        Category saved = categoryRepository.save(category);

        return categoryMapper.toResponse(saved);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Category not found with id " + id));

        categoryRepository.deleteById(id);
    }

}
