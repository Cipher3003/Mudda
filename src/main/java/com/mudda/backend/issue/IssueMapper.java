package com.mudda.backend.issue;

import com.mudda.backend.location.LocationDTO;
import com.mudda.backend.user.MuddaUser;

public class IssueMapper {

    public static Issue toIssue(long userId, CreateIssueRequest issueRequest) {
        return new Issue(
                issueRequest.title(),
                issueRequest.description(),
                userId,
                issueRequest.locationId(),
                issueRequest.categoryId(),
                issueRequest.mediaUrls());
    }

    public static IssueResponse toResponse(Issue issue, MuddaUser muddaUser, LocationDTO locationSummary,
                                           String category, long voteCount, boolean hasUserLiked,
                                           boolean canUserVote, boolean canUserComment,
                                           boolean canUserEdit, boolean canUserDelete) {
        return new IssueResponse(
                // Issue details
                issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getStatus(),
                locationSummary,
                category,
                voteCount,
                issue.getMediaUrls(),
                issue.getSeverityScore(),
                issue.getCreatedAt(),
                issue.getUpdatedAt(),
                // Author details
                muddaUser.getUserId(),
                muddaUser.getUserName(),
                muddaUser.getProfileImageUrl(),
                // Flags
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
                issue.getStatus());
    }

    public static IssueSummaryResponse toSummary(Issue issue, MuddaUser muddaUser, long voteCount,
                                                 boolean hasUserVoted, boolean canUserVote) {
        return new IssueSummaryResponse(
                issue.getId(),
                issue.getTitle(),
                issue.getStatus(),
                voteCount,
                issue.getMediaUrls(),
                issue.getCreatedAt(),
                // Author details
                muddaUser.getUserId(),
                muddaUser.getUserName(),
                muddaUser.getProfileImageUrl(),
                // Flags
                hasUserVoted,
                canUserVote);
    }
}
