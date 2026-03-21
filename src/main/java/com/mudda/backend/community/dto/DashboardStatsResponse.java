package com.mudda.backend.community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mudda.backend.initiative.dto.InitiativeResponse;

import java.util.List;

/**
 * Aggregated dashboard stats for a community admin.
 */
public record DashboardStatsResponse(
        @JsonProperty("community_id") Long communityId,
        @JsonProperty("verified_resident_count") long verifiedResidentCount,
        @JsonProperty("pending_member_count") long pendingMemberCount,
        @JsonProperty("open_issue_count") long openIssueCount,
        @JsonProperty("resolved_issue_count") long resolvedIssueCount,
        @JsonProperty("resolution_rate") double resolutionRate,
        @JsonProperty("active_initiative_count") long activeInitiativeCount,
        @JsonProperty("active_initiatives") List<InitiativeResponse> activeInitiatives
) {}
