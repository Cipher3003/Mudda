package com.mudda.backend.postgres.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.mudda.backend.postgres.models.IssueCategory;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<IssueCategory, Long> {
    
    public Optional<IssueCategory> findByName(String name);
}
