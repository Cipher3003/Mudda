package com.mudda.backend.category;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // region Queries (Read Operations)

    @Override
    public List<CategoryResponse> findAllCategories(@RequestParam String search) {
        log.info("Find all categories");
        if (search == null || search.isBlank())
            return categoryRepository.findAllProjectedBy();

        return categoryRepository.findByNameContainingIgnoreCase(search);
    }

    @Override
    public Optional<CategoryResponse> findById(long id) {
        return categoryRepository.findProjectedById(id);
    }

    // endregion

    // region Commands (Write Operation)

    @Transactional
    @Override
    public CategoryResponse createCategory(CreateCategoryRequest categoryRequest) {
        Category saved = categoryRepository.save(new Category(categoryRequest.name()));
        log.info("Created a category: {}", categoryRequest.name());
        return CategoryMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public List<Long> createCategories(List<CreateCategoryRequest> categoryRequests) {
        List<Category> categories = categoryRepository.saveAll(
                categoryRequests
                        .stream()
                        .map(categoryRequest ->
                                new Category(categoryRequest.name()))
                        .toList()
        );
        log.info("Created {} categories from request", categories.size());
        return categories.stream().map(Category::getId).toList();
    }

//    TODO: the createCategories and saveCategories do the same thing merge them

    @Transactional
    @Override
    public void saveCategories(List<Category> categories) {
        categoryRepository.saveAll(categories);
        log.info("Created {} categories", categories.size());
    }

    @Transactional
    @Override
    public void deleteCategory(long id) {
        categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + id));

        categoryRepository.deleteById(id);
        log.info("Deleted category with id {}", id);
    }

    // endregion

}
