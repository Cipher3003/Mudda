package com.mudda.backend.issue;

import com.mudda.backend.issue.dto.*;
import com.mudda.backend.issue.dto.CoordinateDTO;
import com.mudda.backend.user.MuddaUser;
import org.locationtech.jts.geom.Coordinate;

import java.util.List;

public class IssueMapper {

    public static Issue toIssue(long userId, CreateIssueRequest issueRequest) {
        Coordinate coordinate = new Coordinate(issueRequest.coordinate().x(), issueRequest.coordinate().y());

        return new Issue(
                issueRequest.title(),
                issueRequest.description(),
                userId,
                IssueCategory.valueOf(issueRequest.category()),
                issueRequest.pinCode(),
                issueRequest.city(),
                issueRequest.state(),
                PointFactory.createPoint(coordinate)
        );
    }

    public static IssueResponse toResponse(
            IssueDetailProjection projection, List<String> mediaUrls,
            boolean canVote, boolean canComment,
            boolean canEdit, boolean canDelete
    ) {
        boolean issueDeleted = projection.getIssueDeletedAt() != null;
        boolean authorDeleted = projection.getUserDeletedAt() != null;

        return issueDeleted
                ? new IssueResponse(
                projection.getId(),
                projection.getTitle(),
                null,
                projection.getStatus(),
                projection.getCategory(),
                projection.getCity(),
                projection.getState(),
                null,
                projection.getVoteCount(),
                projection.getCommentCount(),
                0,
                null,
                null,
                null,
                // Author details
                projection.getUserId(),
                "Deleted User",
                null,
                // Flags
                projection.getHasLiked(),
                false, // TODO: force stop interactions on comment, like, votes service
                false,
                canEdit,
                false
        )
                : new IssueResponse(
                // Issue details
                projection.getId(),
                projection.getTitle(),
                projection.getDescription(),
                projection.getStatus(),
                projection.getCategory(),
                projection.getCity(),
                projection.getState(),
                CoordinateDTO.from(projection.getCoordinate()),
                projection.getVoteCount(),
                projection.getCommentCount(),
                projection.getSeverityScore(),
                projection.getCreatedAt(),
                projection.getUpdatedAt(),
                mediaUrls,
                // Author details
                projection.getUserId(),
                authorDeleted ? "Deleted User" : projection.getUsername(),
                authorDeleted ? null : projection.getProfileImageUrl(),
                // Flags
                projection.getHasLiked(),
                canVote,
                canComment,
                canEdit,
                canDelete
        );
    }

    public static IssueResponse toResponse(
            Issue issue, List<String> mediaUrls,
            MuddaUser muddaUser,
            boolean hasLiked,
            boolean canVote, boolean canComment,
            boolean canEdit, boolean canDelete
    ) {
        boolean userDeleted = muddaUser.getDeletedAt() != null;
        return new IssueResponse(
                // Issue details
                issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getStatus(),
                issue.getCategory(),
                issue.getCity(),
                issue.getState(),
                CoordinateDTO.from(issue.getCoordinate()),
                issue.getVoteCount(),
                issue.getCommentCount(),
                issue.getSeverityScore(),
                issue.getCreatedAt(),
                issue.getUpdatedAt(),
                mediaUrls,
                // Author details
                muddaUser.getUserId(),  // TODO: remove delete userId
                userDeleted ? "Deleted User" : muddaUser.getUsername(),
                userDeleted ? null : muddaUser.getProfileImageUrl(),
                // Flags
                hasLiked,
                canVote,
                canComment,
                canEdit,
                canDelete
        );
    }

    public static IssueUpdateResponse toUpdateResponse(Issue issue) {
        return new IssueUpdateResponse(
                issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getStatus());
    }

    public static IssueSummaryResponse toSummary(
            Issue issue, List<String> mediaUrls,
            MuddaUser muddaUser,
            boolean hasVoted, boolean canVote
    ) {
        return new IssueSummaryResponse(
                issue.getId(),
                issue.getTitle(),
                issue.getStatus(),
                issue.getVoteCount(),
                mediaUrls,
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
            Issue issue, List<String> mediaUrls,
            MuddaUser muddaUser
    ) {
        return new IssueDashboardResponse(
                issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getPinCode(),
                issue.getCity(),
                issue.getState(),
                CoordinateDTO.from(issue.getCoordinate()),
                issue.getStatus(),
                issue.getCategory(),
                issue.getVoteCount(),
                issue.getSeverityScore(),
                issue.getCreatedAt(),
                issue.getUpdatedAt(),
                mediaUrls,
                muddaUser.getUserId(),
                muddaUser.getUsername(),
                muddaUser.getProfileImageUrl()
        );
    }
}
