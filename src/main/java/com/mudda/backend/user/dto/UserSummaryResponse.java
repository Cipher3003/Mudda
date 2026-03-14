package com.mudda.backend.user.dto;

public record UserSummaryResponse(
        Long userId,
        String username,
        String profileImageUrl
) {
}
