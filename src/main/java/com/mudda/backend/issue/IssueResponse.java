package com.mudda.backend.issue;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record IssueResponse(
        Long id,
        String title,
        String description,
        IssueStatus status,
        @JsonProperty("reporter_id") Long userId,
        @JsonProperty("location_id") Long locationId,
        @JsonProperty("category_id") Long categoryId,
        @JsonProperty("media_urls") List<String> mediaUrls,
        @JsonProperty("severity_score") double severityScore,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt
) {
}
