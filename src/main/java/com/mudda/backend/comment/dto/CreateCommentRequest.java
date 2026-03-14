package com.mudda.backend.comment.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateCommentRequest(
        @NotNull @NotEmpty String text
) {
}
