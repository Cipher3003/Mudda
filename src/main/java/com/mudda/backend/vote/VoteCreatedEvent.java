/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : IssueCreatedEvent
 * Author  : Vikas Kumar
 * Created : 15-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.vote;

public record VoteCreatedEvent(
        Long issueId, Long userId
) {
}
