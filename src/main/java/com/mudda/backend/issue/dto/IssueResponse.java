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
        Long voteCount,
        List<String> mediaUrls,
        double severityScore,
        Instant createdAt,
        Instant updatedAt,
        // Author
        Long userId,
        String userName,
        String profileImageUrl,
        // Location
        String city,
        String state,
        CoordinateDTO coordinate,
        // FLAGS
        Boolean hasVoted,
        Boolean canVote,
        Boolean canComment,
        Boolean canEdit,
        Boolean canDelete
) {
}
