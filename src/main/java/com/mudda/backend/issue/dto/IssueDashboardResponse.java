package com.mudda.backend.issue.dto;

import com.mudda.backend.issue.IssueStatus;
import com.mudda.backend.location.dto.LocationResponse;

import java.time.Instant;
import java.util.List;

public record IssueDashboardResponse(
        Long id,
        String title,
        String description,
        IssueStatus status,
        LocationResponse location,
        String category,
        Long voteCount,
        List<String> mediaUrls,
        double severityScore,
        Instant createdAt,
        Instant updatedAt,
        // Author
        Long userId,
        String userName,
        String profileImageUrl) {

}
