package com.mudda.backend.comment.dto;

import java.time.Instant;

public record CommentResponse(
        Long id,
        String text,
        Long userId,
        Long issueId,
        Long parentId,
        Long likeCount,
        Long replyCount,
        Instant createdAt,
//        FLAGS
        Boolean hasLiked,
        Boolean canLike,
        Boolean canUpdate,
        Boolean canDelete
) {
}
