package com.mudda.backend.issue.dto;

import com.mudda.backend.issue.IssueStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

// TODO: add latitude, longitude and radius for coordinate based filtering
public record IssueFilterRequest(
        String search,
        IssueStatus status,
        Long userId,    // TODO: remove fields not needed or makes no sense in filter request
        Long categoryId,
        String city,
        String state,
        Boolean urgency,
        Double minSeverity,
        Double maxSeverity,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant createdAfter,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant createdBefore
) {
}
