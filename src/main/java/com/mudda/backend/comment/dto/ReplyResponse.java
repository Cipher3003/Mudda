package com.mudda.backend.comment.dto;

import java.time.Instant;

public record ReplyResponse(
        Long id,
        String text,
        Long userId,
        Long parentId,
        Long likeCount,
        Instant createdAt,
//        FLAGS
        Boolean hasLiked,
        Boolean canLike,
        Boolean canUpdate,
        Boolean canDelete
) {
}
