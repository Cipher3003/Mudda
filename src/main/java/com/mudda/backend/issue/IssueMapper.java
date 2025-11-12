package com.mudda.backend.issue;

import com.mudda.backend.location.LocationDTO;

public class IssueMapper {

    public static Issue toIssue(CreateIssueRequest issueRequest) {
        return new Issue(
                issueRequest.title(),
                issueRequest.description(),
                issueRequest.userId(),
                issueRequest.locationId(),
                issueRequest.categoryId(),
                issueRequest.mediaUrls()
        );
    }

    public static IssueResponse toResponse(Issue issue, LocationDTO locationSummary,
                                           String category, long voteCount, boolean hasUserLiked) {
        return new IssueResponse(
                issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getStatus(),
                issue.getUserId(),
                locationSummary,
                category,
                voteCount,
                hasUserLiked,
                issue.getMediaUrls(),
                issue.getSeverityScore(),
                issue.getCreatedAt(),
                issue.getUpdatedAt()
        );
    }

    public static IssueUpdateResponse toResponse(Issue issue) {
        return new IssueUpdateResponse(
                issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getStatus()
        );
    }

    public static IssueSummaryResponse toSummary(Issue issue, long voteCount, boolean hasUserLiked) {
        return new IssueSummaryResponse(
                issue.getId(),
                issue.getTitle(),
                issue.getStatus(),
                voteCount,
                hasUserLiked,
                issue.getMediaUrls(),
                issue.getCreatedAt()
        );
    }
}
