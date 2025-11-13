package com.mudda.backend.issue;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;

import java.util.List;
import java.util.Optional;

public interface IssueService {

    Page<IssueSummaryResponse> findAllIssues(IssueFilterRequest filterRequest, Pageable pageable, long userId);

    Optional<IssueResponse> findById(long id, long userId);

    IssueResponse createIssue(CreateIssueRequest issueRequest);

    IssueUpdateResponse updateIssue(long id, UpdateIssueRequest issueRequest);

    /**
     * Deletes an Issue and cleanup all related entities to it.
     */
    void deleteIssue(long id);

    void deleteAllIssuesByUser(long id);
}
