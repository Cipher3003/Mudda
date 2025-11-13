package com.mudda.backend.issue;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record IssueSummaryResponse(
        Long id,
        String title,
        IssueStatus status,
        @JsonProperty("vote_count") Long voteCount,
        @JsonProperty("has_user_voted") Boolean hasUserVoted,
        @JsonProperty("media_urls") List<String> mediaUrls,
        @JsonProperty("created_at") Instant createdAt
) {
}
