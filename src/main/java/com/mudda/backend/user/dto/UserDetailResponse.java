package com.mudda.backend.user.dto;

import com.mudda.backend.user.MuddaUserRole;

import java.time.Instant;

public record UserDetailResponse(
        Long userId,
        String username,
        String Name,
        String phoneNumber,
        String email,
        String profileImageUrl,
        MuddaUserRole role,
        Instant createdAt,
        Boolean locked,
        Boolean enabled
) {
}
