package com.mudda.backend.social.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record UserProfileResponse(
        @JsonProperty("user_id") Long userId,
        String username,
        String name,
        @JsonProperty("avatar_url") String avatarUrl,
        @JsonProperty("follower_count") long followerCount,
        @JsonProperty("following_count") long followingCount
) {}
