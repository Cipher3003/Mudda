package com.mudda.backend.initiative;

import com.mudda.backend.initiative.dto.InitiativeResponse;

/**
 * Mapper between CommunityInitiative domain entity and its DTOs.
 */
public class InitiativeMapper {

    private InitiativeMapper() {}

    public static InitiativeResponse toResponse(CommunityInitiative initiative, long participantCount) {
        return new InitiativeResponse(
                initiative.getId(),
                initiative.getTitle(),
                initiative.getDescription(),
                initiative.getType(),
                initiative.getTargetMetric(),
                initiative.getCurrentMetric(),
                initiative.getProgressPercentage(),
                participantCount,
                initiative.getEventDate(),
                initiative.isActive(),
                initiative.isComplete(),
                initiative.getCreatedAt()
        );
    }
}
