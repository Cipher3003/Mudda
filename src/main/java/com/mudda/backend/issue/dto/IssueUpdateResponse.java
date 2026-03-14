/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : IssueUpdateResponse
 * Author  : Vikas Kumar
 * Created : 12-11-2025
 * ---------------------------------------------------------------
 */
package com.mudda.backend.issue.dto;

import com.mudda.backend.issue.IssueStatus;

public record IssueUpdateResponse(
        Long id,
        String title,
        String description,
        IssueStatus status
) {
}
