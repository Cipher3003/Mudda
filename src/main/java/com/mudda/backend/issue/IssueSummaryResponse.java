package com.mudda.backend.issue;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record IssueSummaryResponse(
        Long id,
        String title,
        IssueStatus status,
        @JsonProperty("media_urls") List<String> mediaUrls
) {
}
