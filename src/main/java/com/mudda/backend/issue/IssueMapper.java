package com.mudda.backend.issue;

public class IssueMapper {
    public static IssueResponse toResponse(Issue issue) {
        return new IssueResponse(
                issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getStatus(),
                issue.getUserId(),
                issue.getLocationId(),
                issue.getCategoryId(),
                issue.getMediaUrls(),
                issue.getSeverityScore(),
                issue.getCreatedAt(),
                issue.getUpdatedAt()
        );
    }

    public static IssueSummaryResponse toSummary(Issue issue) {
        return new IssueSummaryResponse(
                issue.getId(),
                issue.getTitle(),
                issue.getStatus(),
                issue.getMediaUrls()
        );
    }
}
