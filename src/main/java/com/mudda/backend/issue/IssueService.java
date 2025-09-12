package com.mudda.backend.issue;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;

import java.util.List;
import java.util.Optional;

public interface IssueService {

    Page<IssueSummaryResponse> findAllIssues(IssueFilterRequest filterRequest, Pageable pageable);

    Optional<IssueResponse> findById(Long id);

    List<IssueSummaryResponse> findByUserId(Long userId);

    List<IssueSummaryResponse> findByCategoryId(Long categoryId);

    // TODO: refactor and correct
    @Deprecated
    List<Issue> findByLocation_CoordinatesNear(Point point, int distance);

    IssueResponse createIssue(CreateIssueRequest issueRequest);

    IssueResponse updateIssue(Long id, UpdateIssueRequest issueRequest);

    /**
     * Deletes an Issue and cleanup all related entities to it.
     */
    void deleteIssue(Long id);
}
