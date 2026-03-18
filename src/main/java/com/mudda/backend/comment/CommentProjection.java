/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : CommentProjection
 * Author  : Vikas Kumar
 * Created : 16-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.comment;

import java.time.Instant;

public interface CommentProjection {

    Long getId();

    String getText();

    Long getUserId();

    Long getIssueId();

    Long getParentId();

    Long getLikeCount();

    Long getReplyCount();

    Instant getCreatedAt();

    Boolean getHasLiked();

}
