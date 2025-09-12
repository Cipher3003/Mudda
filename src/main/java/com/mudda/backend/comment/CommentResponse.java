package com.mudda.backend.comment;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CommentResponse(
        @JsonProperty("comment_id") Long commentId,
        String text,
        @JsonProperty("like_count") Long likeCount
) {
}
