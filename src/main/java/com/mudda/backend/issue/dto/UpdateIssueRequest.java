package com.mudda.backend.issue.dto;

import com.mudda.backend.issue.IssueStatus;

public record UpdateIssueRequest(
        String title,
        String description,
        IssueStatus status
) {
}
