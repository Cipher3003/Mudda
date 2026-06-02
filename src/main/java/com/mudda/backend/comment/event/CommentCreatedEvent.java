/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : CommentCreatedEvent
 * Author  : Vikas Kumar
 * Created : 15-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.comment.event;

public record CommentCreatedEvent(
        Long issueId
) {
}
