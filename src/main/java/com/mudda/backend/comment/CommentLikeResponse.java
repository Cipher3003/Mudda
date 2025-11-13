package com.mudda.backend.comment;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CommentLikeResponse(
        Boolean liked,
        @JsonProperty("like_count") Long likeCount) {
}
