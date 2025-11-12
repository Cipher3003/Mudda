package com.mudda.backend.comment;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CommentDetailResponse(
        @JsonProperty("comment_id") Long commentId,
        String text,
        @JsonProperty("author_id") Long userId,
        @JsonProperty("issue_id") Long issueId,
        @JsonProperty("like_count") Long likeCount,
        @JsonProperty("reply_count") Long repliesCount,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("has_user_liked") Boolean hasUserLiked
) {
}
