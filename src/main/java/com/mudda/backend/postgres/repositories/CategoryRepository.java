package com.mudda.backend.postgres.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.mudda.backend.postgres.models.Category;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    public Optional<Category> findByName(String name);
}
