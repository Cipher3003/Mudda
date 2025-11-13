package com.mudda.backend.comment;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CommentResponse(
        @JsonProperty("comment_id") Long commentId,
        String text,
        @JsonProperty("author_id") Long userId,
        @JsonProperty("issue_id") Long issueId,
        @JsonProperty("created_at") Instant createdAt) {
}