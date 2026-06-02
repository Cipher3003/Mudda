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

    Long getIssueId();

    Long getParentId();

    Long getLikeCount();

    Long getReplyCount();

    Instant getCreatedAt();

    Long getUserId();

    String getUsername();

    String getProfileImageUrl();

    Instant getAuthorDeletedAt();

    Boolean getHasLiked();

}
