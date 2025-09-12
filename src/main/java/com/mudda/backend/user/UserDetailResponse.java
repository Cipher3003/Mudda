package com.mudda.backend.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record UserDetailResponse(
        @JsonProperty("user_id") Long userId,
        @JsonProperty("username") String userName,
        String Name,
        @JsonProperty("phone_number") String phoneNumber,
        String email,
        @JsonProperty("profile_image_url") String profileImageUrl,
        @JsonProperty("role_id") Long roleId,
        @JsonProperty("created_at") Instant createdAt
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
