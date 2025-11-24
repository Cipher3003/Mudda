package com.mudda.backend.issue;

import java.util.Map;

public record IssueClusterDTO(
        Double latitude,
        Double longitude,
        String topCategory,
        Map<String, Long> issueCounts
) {

}
