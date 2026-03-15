/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : CommentRemovedEvent
 * Author  : Vikas Kumar
 * Created : 15-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.comment.event;

public record CommentRemovedEvent(
        Long issueId,
        Long id
) {
}
