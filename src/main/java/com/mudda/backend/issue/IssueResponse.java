package com.mudda.backend.issue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mudda.backend.location.LocationDTO;

import java.time.Instant;
import java.util.List;

public record IssueResponse(
        Long id,
        String title,
        String description,
        IssueStatus status,
        @JsonProperty("reporter_id") Long userId,
        LocationDTO locationSummary,
        String category,
        @JsonProperty("vote_count") Long voteCount,
        @JsonProperty("has_user_voted") Boolean hasUserVoted,
        @JsonProperty("media_urls") List<String> mediaUrls,
        @JsonProperty("severity_score") double severityScore,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt
) {
}
