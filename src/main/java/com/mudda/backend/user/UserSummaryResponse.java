package com.mudda.backend.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserSummaryResponse(
        @JsonProperty("user_id") Long userId,
        @JsonProperty("username") String userName,
        @JsonProperty("profile_image_url") String profileImageUrl
) {

    public static UserSummaryResponse from(User user) {
        return new UserSummaryResponse(
                user.getUserId(),
                user.getUserName(),
                user.getProfileImageUrl());
    }
}
