package com.mudda.backend.issue;

public record UpdateIssueRequest(
        String title,
        String description,
        IssueStatus status
) {
}
