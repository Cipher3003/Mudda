package com.mudda.backend.issue;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

// TODO: add latitude, longitude and radius for coordinate based filtering
public record IssueFilterRequest(
        String search,
        IssueStatus status,
        Long userId,
        @JsonProperty("category_id") Long categoryId,
        String city,
        String state,
        Boolean urgency,
        @JsonProperty("min_severity") Double minSeverity,
        @JsonProperty("max_severity") Double maxSeverity,

        @JsonProperty("created_after")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant createdAfter,

        @JsonProperty("created_before")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant createdBefore
) {
}
