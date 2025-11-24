package com.mudda.backend.issue;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IssueService {

    Page<IssueSummaryResponse> findAllIssues(IssueFilterRequest filterRequest, Pageable pageable, Long userId);

    Optional<IssueResponse> findById(long id, Long userId);

    IssueClusterResponse findAllIssueClusters(IssueClusterRequest clusterRequest);

    IssueResponse createIssue(Long userId, CreateIssueRequest issueRequest);

    IssueUpdateResponse updateIssue(long id, Long userId, UpdateIssueRequest issueRequest);

    /**
     * Deletes an Issue and cleanup all related entities to it.
     */
    void deleteIssue(long id, Long userId);

    void deleteAllIssuesByUser(long id);
}
