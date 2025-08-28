package com.mudda.backend.postgres.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mudda.backend.postgres.models.Issue;
import com.mudda.backend.postgres.models.IssueStatus;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    List<Issue> findByStatus(IssueStatus status);

    List<Issue> findByUserId(Long userId);

    List<Issue> findByCategoryId(Long categoryId);

}