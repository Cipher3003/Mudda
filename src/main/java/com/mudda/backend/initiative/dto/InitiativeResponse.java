package com.mudda.backend.initiative.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mudda.backend.initiative.InitiativeType;

import java.time.Instant;

/**
 * Outbound representation of a community initiative.
 */
public record InitiativeResponse(
        Long id,
        String title,
        String description,
        InitiativeType type,
        @JsonProperty("target_metric") int targetMetric,
        @JsonProperty("current_metric") int currentMetric,
        @JsonProperty("progress_percentage") double progressPercentage,
        @JsonProperty("participant_count") long participantCount,
        @JsonProperty("event_date") Instant eventDate,
        boolean active,
        @JsonProperty("is_complete") boolean complete,
        @JsonProperty("created_at") Instant createdAt
) {}
