package com.mudda.backend.issue;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateIssueRequest(
        @NotEmpty String title,
        @NotEmpty String description,
        @NotNull Long locationId,
        @NotNull Long categoryId,
        @Size(max = 5) List<String> mediaUrls
) {
}
