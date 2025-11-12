package com.mudda.backend.issue;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

// TODO: add latitude, longitude and radius for coordinate based filtering
public record IssueFilterRequest(
        String search,
        IssueStatus status,
        Long userId,
        Long categoryId,
        String city,
        String state,
        Boolean urgency,
        Double minSeverity,
        Double maxSeverity,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdAfter,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdBefore
) {
}
