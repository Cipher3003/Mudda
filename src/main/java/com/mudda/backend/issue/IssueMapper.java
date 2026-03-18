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
            Issue issue, List<String> mediaUrls,
            MuddaUser muddaUser,
            boolean hasLiked,
            boolean canVote, boolean canComment,
            boolean canEdit, boolean canDelete
    ) {
        return new IssueResponse(
                // Issue details
                issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getStatus(),
                issue.getCategory(),
                issue.getVoteCount(),
                mediaUrls,
                issue.getSeverityScore(),
                issue.getCreatedAt(),
                issue.getUpdatedAt(),
                // Author details
                muddaUser.getUserId(),
                muddaUser.getUsername(),
                muddaUser.getProfileImageUrl(),
                issue.getCity(),
                issue.getState(),
                CoordinateDTO.from(issue.getCoordinate()),
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
            MuddaUser muddaUser,
            long voteCount
    ) {
        return new IssueDashboardResponse(
                issue.getId(),
                issue.getTitle(),
                issue.getDescription(),
                issue.getStatus(),
                issue.getCategory(),
                voteCount,
                mediaUrls,
                issue.getSeverityScore(),
                issue.getCreatedAt(),
                issue.getUpdatedAt(),
                muddaUser.getUserId(),
                muddaUser.getUsername(),
                muddaUser.getProfileImageUrl(),
                issue.getPinCode(),
                issue.getCity(),
                issue.getState(),
                CoordinateDTO.from(issue.getCoordinate())
        );
    }
}
