package com.mudda.backend.postgres.controllers;

import com.mudda.backend.postgres.models.IssueCategory;
import com.mudda.backend.postgres.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<IssueCategory>> getAll() {
        return ResponseEntity.ok(categoryService.findAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueCategory> getById(@PathVariable Long id) {
        return categoryService.findCategoryById(id)
                .map(ResponseEntity::ok) // 200 ok
                .orElse(ResponseEntity.notFound().build()); // 404 not found
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<IssueCategory> getByName(@PathVariable String name) {
        return categoryService.findCategoryByName(name)
                .map(ResponseEntity::ok) // 200 ok
                .orElse(ResponseEntity.notFound().build()); // 404 not found
    }

    // TODO: Validate input
    @PostMapping
    public ResponseEntity<IssueCategory> create(@RequestBody IssueCategory category) {
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    // TODO: not found check
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
