package com.mudda.backend.issue;

import com.mudda.backend.location.LocationDTO;

public class IssueMapper {

    public static Issue toIssue(long userId, CreateIssueRequest issueRequest) {
        return new Issue(
                issueRequest.title(),
                issueRequest.description(),
                userId,
                issueRequest.locationId(),
                issueRequest.categoryId(),
                issueRequest.mediaUrls()
        );
    }

    public static IssueResponse toResponse(Issue issue, LocationDTO locationSummary,
                                           String category, long voteCount, boolean hasUserLiked,
                                           boolean canUserVote, boolean canUserComment,
                                           boolean canUserEdit, boolean canUserDelete
    ) {
        return new IssueResponse(
                issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getStatus(),
                issue.getUserId(),
                locationSummary,
                category,
                voteCount,
                issue.getMediaUrls(),
                issue.getSeverityScore(),
                issue.getCreatedAt(),
                issue.getUpdatedAt(),
                hasUserLiked,
                canUserVote,
                canUserComment,
                canUserEdit,
                canUserDelete
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

    public static IssueSummaryResponse toSummary(Issue issue, long voteCount,
                                                 boolean hasUserVoted, boolean canUserVote
    ) {
        return new IssueSummaryResponse(
                issue.getId(),
                issue.getTitle(),
                issue.getStatus(),
                voteCount,
                issue.getMediaUrls(),
                issue.getCreatedAt(),
                hasUserVoted,
                canUserVote
        );
    }
}
