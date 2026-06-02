package com.mudda.backend.issue.dto;

import com.mudda.backend.validator.ValidEnum;
import com.mudda.backend.issue.IssueCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateIssueRequest(
        @NotEmpty String title,
        @NotEmpty String description,
        @NotNull
        @ValidEnum(
                enumClass = IssueCategory.class,
                message = "Invalid category value. Valid category values: [INF, SAN, ELE]"
        )
        String category,
        @NotNull @Size(max = 5) List<String> mediaUrls,
        @NotNull @NotBlank String pinCode,
        @NotNull @NotBlank String city,
        @NotNull @NotBlank String state,
        @NotNull CoordinateDTO coordinate
) {
}
