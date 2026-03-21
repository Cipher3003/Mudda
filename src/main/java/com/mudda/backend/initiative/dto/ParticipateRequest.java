package com.mudda.backend.initiative.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for participating in (RSVP / pledging to) an initiative.
 */
public record ParticipateRequest(
        @NotNull @Min(1) Integer pledgedMetric
) {}
