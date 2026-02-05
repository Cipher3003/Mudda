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
        MuddaUserRole role,
        @JsonProperty("created_at") Instant createdAt,
        Boolean locked,
        Boolean enabled
) {
}
