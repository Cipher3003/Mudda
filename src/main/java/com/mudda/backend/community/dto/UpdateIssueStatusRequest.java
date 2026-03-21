package com.mudda.backend.community.dto;

import com.mudda.backend.issue.IssueStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for an admin updating an issue's status and optionally
 * appending an official response.
 */
public record UpdateIssueStatusRequest(
        @NotNull IssueStatus status,
        String officialResponse
) {}
