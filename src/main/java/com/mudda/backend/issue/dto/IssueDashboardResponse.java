package com.mudda.backend.issue.dto;

import com.mudda.backend.issue.IssueCategory;
import com.mudda.backend.issue.IssueStatus;

import java.time.Instant;
import java.util.List;

public record IssueDashboardResponse(
        Long id,
        String title,
        String description,
        String pinCode,
        String city,
        String state,
        CoordinateDTO coordinate,
        IssueStatus status,
        IssueCategory category,
        Long voteCount,
        double severityScore,
        Instant createdAt,
        Instant updatedAt,

        List<String> mediaUrls,
        // Author
        Long userId,
        String userName,
        String profileImageUrl
) {
}
