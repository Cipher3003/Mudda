package com.mudda.backend.issue;

import com.mudda.backend.issue.dto.*;
import com.mudda.backend.location.dto.LocationDTO;
import com.mudda.backend.location.dto.LocationResponse;
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

    public static IssueResponse toResponse(
            Issue issue, MuddaUser muddaUser, LocationDTO locationSummary,
            String category, long voteCount, boolean hasLiked,
            boolean canVote, boolean canComment,
            boolean canEdit, boolean canDelete
    ) {
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
                muddaUser.getUsername(),
                muddaUser.getProfileImageUrl(),
                // Flags
                hasLiked,
                canVote,
                canComment,
                canEdit,
                canDelete
        );
    }

    public static IssueUpdateResponse toResponse(Issue issue) {
        return new IssueUpdateResponse(
                issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getStatus());
    }

    public static IssueSummaryResponse toSummary(
            Issue issue, MuddaUser muddaUser, long voteCount,
            boolean hasVoted, boolean canVote
    ) {
        return new IssueSummaryResponse(
                issue.getId(),
                issue.getTitle(),
                issue.getStatus(),
                voteCount,
                issue.getMediaUrls(),
                issue.getCreatedAt(),
                // Author details
                muddaUser.getUserId(),
                muddaUser.getUsername(),
                muddaUser.getProfileImageUrl(),
                // Flags
                hasVoted,
                canVote
        );
    }

    public static IssueDashboardResponse forDashboard(
            Issue issue, MuddaUser muddaUser, long voteCount,
            LocationResponse locationResponse, String category
    ) {
        return new IssueDashboardResponse(
                issue.getId(), issue.getTitle(), issue.getDescription(), issue.getStatus(),
                locationResponse, category, voteCount, issue.getMediaUrls(), issue.getSeverityScore(),
                issue.getCreatedAt(), issue.getUpdatedAt(), muddaUser.getUserId(), muddaUser.getUsername(),
                muddaUser.getProfileImageUrl()
        );
    }
}
