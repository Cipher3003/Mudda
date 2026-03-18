/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : CommentUpdateResponse
 * Author  : Vikas Kumar
 * Created : 16-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.comment.dto;

import java.time.Instant;

public record CommentUpdateResponse(
        Long id,
        String text,
        Instant updatedAt
) {
}
