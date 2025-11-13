package com.mudda.backend.comment;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReplyResponse(
        @JsonProperty("reply_id") Long commentId,
        String text,
        @JsonProperty("author_id") Long userId,
        @JsonProperty("comment_id") Long parentId,
        @JsonProperty("like_count") Long likeCount,
        @JsonProperty("created_at") Instant createdAt,
//        FLAGS
        @JsonProperty("has_user_liked") Boolean hasUserLiked,
        @JsonProperty("can_user_like") Boolean canUserLike,
        @JsonProperty("can_user_update") Boolean canUserUpdate,
        @JsonProperty("can_user_delete") Boolean canUserDelete
) {
}
