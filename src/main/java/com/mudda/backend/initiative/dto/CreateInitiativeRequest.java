package com.mudda.backend.initiative.dto;

import com.mudda.backend.initiative.InitiativeType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * Request body for creating a new community initiative.
 */
public record CreateInitiativeRequest(
        @NotBlank @Size(max = 200) String title,
        String description,
        @NotNull InitiativeType type,
        @NotNull @Min(1) Integer targetMetric,
        Instant eventDate
) {}
