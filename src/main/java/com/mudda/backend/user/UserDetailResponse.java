package com.mudda.backend.user;

import java.time.Instant;

public record UserDetailResponse(
        Long userId,
        String userName,
        String Name,
        String phoneNumber,
        String email,
        String profileImageUrl,
        Long roleId,
        Instant createdAt
) {
    public static UserDetailResponse from(User user) {
        return new UserDetailResponse(
                user.getUserId(),
                user.getUserName(),
                user.getName(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getProfileImageUrl(),
                user.getRoleId(),
                user.getCreatedAt()
        );
    }
}
