package com.mudda.backend.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mudda.backend.community.CommunityRole;
import com.mudda.backend.community.MemberStatus;

import java.time.Instant;

/**
 * Outbound representation of a community membership.
 */
public record CommunityMemberResponse(
        @JsonProperty("member_id") Long memberId,
        @JsonProperty("user_id") Long userId,
        @JsonProperty("username") String username,
        @JsonProperty("profile_image_url") String profileImageUrl,
        CommunityRole role,
        MemberStatus status,
        @JsonProperty("joined_at") Instant joinedAt
) {}
