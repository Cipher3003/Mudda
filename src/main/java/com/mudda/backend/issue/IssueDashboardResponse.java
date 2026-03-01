package com.mudda.backend.issue;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mudda.backend.location.LocationResponse;

public record IssueDashboardResponse(
        Long id,
        String title,
        String description,
        IssueStatus status,
        LocationResponse location,
        String category,
        @JsonProperty("vote_count") Long voteCount,
        @JsonProperty("media_urls") List<String> mediaUrls,
        @JsonProperty("severity_score") double severityScore,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        // Author
        @JsonProperty("author_id") Long userId,
        @JsonProperty("author_name") String userName,
        @JsonProperty("author_image_url") String profileImageUrl) {

}
