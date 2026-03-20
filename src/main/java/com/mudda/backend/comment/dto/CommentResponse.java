package com.mudda.backend.comment.dto;

import java.time.Instant;

public record CommentResponse(
        Long id,
        String text,
        Long issueId,
        Long parentId,
        Long likeCount,
        Long replyCount,
        Instant createdAt,

        Long userId,
        String username,
        String profileImage,
//        FLAGS
        Boolean hasLiked,
        Boolean canLike,
        Boolean canUpdate,
        Boolean canDelete
) {
}
