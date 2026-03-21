package com.mudda.backend.community.announcement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * Outbound representation of a community announcement.
 */
public record AnnouncementResponse(
        Long id,
        @JsonProperty("community_id") Long communityId,
        @JsonProperty("author_id") Long authorId,
        @JsonProperty("author_name") String authorName,
        String title,
        String body,
        @JsonProperty("created_at") Instant createdAt
) {}
