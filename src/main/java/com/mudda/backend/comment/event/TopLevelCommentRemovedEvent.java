/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : TopLevelCommentRemovedEvent
 * Author  : Vikas Kumar
 * Created : 16-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.comment.event;

public record TopLevelCommentRemovedEvent(Long issueId, long size) {
}
