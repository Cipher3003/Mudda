package com.mudda.backend.issue.dto;

import com.mudda.backend.issue.IssueCategory;
import com.mudda.backend.issue.IssueStatus;

import java.time.Instant;
import java.util.List;

public record IssueResponse(
        Long id,
        String title,
        String description,
        IssueStatus status,
        IssueCategory category,
        // Location
        String city,
        String state,
        CoordinateDTO coordinate,
        Long voteCount,
        Long commentCount,
        double severityScore,
        Instant createdAt,
        Instant updatedAt,  // TOOD: remove this field useless
        // TODO: add deletedAt

        List<String> mediaUrls,
        // Author
        Long userId,
        String userName,
        String profileImageUrl,
        // FLAGS
        Boolean hasVoted,
        Boolean canVote,
        Boolean canComment,
        Boolean canEdit,
        Boolean canDelete
) {
}
