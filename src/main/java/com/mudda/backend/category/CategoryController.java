package com.mudda.backend.category;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/issues/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // ----------- PUBLIC READ -----------------
    // region Queries (Read Operations)

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll(
            @RequestParam(required = false) String search) {
        log.debug("Get all categories request with search {}", search);
        return ResponseEntity.ok(categoryService.findAllCategories(search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable long id) {
        log.debug("Get category by id {}", id);
        return categoryService.findById(id)
                .map(ResponseEntity::ok) // 200 ok
                .orElse(ResponseEntity.notFound().build()); // 404 not found
    }

    // endregion

    // ----------- AUTH COMMANDS -----------------
    // region Commands (Write Operations)

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest categoryRequest) {
        log.debug("Create category request {}", categoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(categoryRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        log.debug("Delete category by id {}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // endregion
}
