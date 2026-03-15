package com.mudda.backend.issue.dto;

import com.mudda.backend.issue.IssueCategory;
import com.mudda.backend.issue.IssueStatus;
import com.mudda.backend.validator.ValidEnum;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

// TODO: add latitude, longitude and radius for coordinate based filtering
public record IssueFilterRequest(
        String search,
        @ValidEnum(enumClass = IssueStatus.class)
        String status,
        Long userId,    // TODO: remove fields not needed or makes no sense in filter request
        @ValidEnum(enumClass = IssueCategory.class)
        String category,
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
