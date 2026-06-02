package com.mudda.backend.comment.dto;

import java.time.Instant;

public record CommentCreatedResponse(
        Long id,
        Instant createdAt
) {
}