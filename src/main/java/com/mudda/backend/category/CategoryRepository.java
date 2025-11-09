package com.mudda.backend.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<CategoryResponse> findAllProjectedBy();

    Optional<CategoryResponse> findProjectedById(Long id);

    List<CategoryResponse> findByNameContainingIgnoreCase(String text);
}
