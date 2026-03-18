package com.mudda.backend.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCommentRequest(
        @NotNull @NotBlank String text,
        Long issueId,
        Long parentId
) {
}
