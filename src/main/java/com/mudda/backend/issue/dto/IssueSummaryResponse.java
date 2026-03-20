package com.mudda.backend.issue.dto;

import com.mudda.backend.issue.IssueStatus;

import java.time.Instant;
import java.util.List;

public record IssueSummaryResponse(
        Long id,
        String title,
        IssueStatus status,
        Long voteCount,
        List<String> mediaUrls,
        Instant createdAt,
        // TODO: add deletedAt, updatedAt other metrics
        // Author
        Long userId,
        String userName,
        String profileImageUrl,
        // FLAGS
        Boolean hasVoted,
        Boolean canVote
) {
}
