package com.mudda.backend.comment.dto;

import java.time.Instant;

public record CommentDetailResponse(
        Long id,
        String text,
        Long userId,
        Long issueId,
        Long likeCount,
        Long repliesCount,
        Instant createdAt,
//        FLAGS
        Boolean hasLiked,
        Boolean canLike,
        Boolean canUpdate,
        Boolean canDelete
) {
}
