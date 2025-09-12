package com.mudda.backend.comment;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReplyResponse(
        @JsonProperty("reply_id") Long commentId,
        String text,
        @JsonProperty("like_count") Long likeCount,
        @JsonProperty("parent_id") Long parentId
) {
}
