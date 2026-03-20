package com.mudda.backend.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request body for creating a new Community.
 *
 * @param name           Human-readable community name.
 * @param description    Optional long-form description.
 * @param geofenceWkt    Well-Known Text (WKT) representation of the Polygon geofence,
 *                       e.g. "POLYGON((lon1 lat1, lon2 lat2, ...))".
 *                       Parsed server-side via JTS {@code WKTReader}.
 */
public record CreateCommunityRequest(
        @NotBlank @Size(max = 120) String name,
        String description,
        @NotNull @NotBlank String geofenceWkt
) {}
