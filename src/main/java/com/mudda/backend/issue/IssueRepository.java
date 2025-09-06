package com.mudda.backend.issue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {

    Optional<IssueResponse> findProjectedById(Long id);

    List<IssueSummaryResponse> findByStatus(IssueStatus status);

    List<IssueSummaryResponse> findByUserId(Long userId);

    List<IssueSummaryResponse> findByCategoryId(Long categoryId);

}