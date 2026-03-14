package com.mudda.backend.comment.dto;

import java.time.Instant;

public record CommentResponse(
        Long id,
        String text,
        Long userId,
        Long issueId,
        Instant createdAt) {
}