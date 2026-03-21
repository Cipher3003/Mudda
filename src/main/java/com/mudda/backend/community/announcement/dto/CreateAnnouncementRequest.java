package com.mudda.backend.community.announcement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for creating a community announcement broadcast.
 */
public record CreateAnnouncementRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank String body
) {}
