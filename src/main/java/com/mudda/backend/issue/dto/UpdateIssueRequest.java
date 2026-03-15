package com.mudda.backend.issue.dto;

import com.mudda.backend.issue.IssueStatus;
import com.mudda.backend.validator.ValidEnum;

public record UpdateIssueRequest(
        String title,
        String description,
        @ValidEnum(enumClass = IssueStatus.class)
        String status
) {
}
