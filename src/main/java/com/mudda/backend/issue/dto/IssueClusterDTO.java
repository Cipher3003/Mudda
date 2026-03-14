package com.mudda.backend.issue.dto;

import java.util.Map;

public record IssueClusterDTO(
        Double latitude,
        Double longitude,
        String topCategory,
        Map<String, Long> issueCounts
) {
}
