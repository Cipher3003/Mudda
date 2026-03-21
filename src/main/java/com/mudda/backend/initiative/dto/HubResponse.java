package com.mudda.backend.initiative.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Aggregated hub screen payload for a community — active initiatives and summary stats.
 */
public record HubResponse(
        @JsonProperty("community_id") Long communityId,
        @JsonProperty("community_name") String communityName,
        @JsonProperty("active_initiative_count") long activeInitiativeCount,
        @JsonProperty("total_participants") long totalParticipants,
        List<InitiativeResponse> initiatives
) {}
