package com.mudda.backend.postgres.services;

import org.springframework.data.geo.Point;

import com.mudda.backend.postgres.models.Issue;
import com.mudda.backend.postgres.models.IssueStatus;

import java.util.List;
import java.util.Optional;

public interface IssueService {

    List<Issue> findAllIssues();

    Optional<Issue> findById(Long id);

    List<Issue> findByStatus(IssueStatus status);

    List<Issue> findByUserId(Long userId);

    List<Issue> findByCategoryId(Long categoryId);

    // TODO: refactor and correct
    List<Issue> findByLocation_CoordinatesNear(Point point, int distance);

    Issue createIssue(Issue issue);

    Issue updateIssue(Long id, Issue issue);

    /**
     * Deletes an Issue and cleanup all related entities to it.
     */
    void deleteIssue(Long id);
}
