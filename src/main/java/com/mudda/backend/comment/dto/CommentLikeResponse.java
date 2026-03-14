package com.mudda.backend.comment.dto;

public record CommentLikeResponse(
        Boolean liked,
        Long likeCount) {
}
