package com.mudda.backend.issue;

/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : IssueUpdateResponse
 * Author  : Vikas Kumar
 * Created : 12-11-2025
 * ---------------------------------------------------------------
 */
public record IssueUpdateResponse(
        Long id,
        String title,
        String description,
        IssueStatus status
) {
}
