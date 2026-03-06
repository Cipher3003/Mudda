/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : IssueCreatedEvent
 * Author  : Vikas Kumar
 * Created : 06-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.issue;

public record IssueCreatedEvent(Long id, Long userId, Boolean isOld, String text) {
}
