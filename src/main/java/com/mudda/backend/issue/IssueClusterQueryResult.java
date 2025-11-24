package com.mudda.backend.issue;

public record IssueClusterQueryResult(
        Double cellX,
        Double cellY,
        String category,
        Long count,
        Double centerLatitude,
        Double centerLongitude
) {
}