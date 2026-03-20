package com.mudda.backend.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mudda.backend.community.CommunityStatus;

import java.time.Instant;

/**
 * Outbound representation of a Community.
 */
public record CommunityResponse(
        Long id,
        String name,
        String description,
        CommunityStatus status,
        @JsonProperty("owner_id") Long ownerId,
        @JsonProperty("resident_count") long residentCount,
        @JsonProperty("created_at") Instant createdAt
) {}
