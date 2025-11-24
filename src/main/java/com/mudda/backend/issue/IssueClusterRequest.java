package com.mudda.backend.issue;

import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record IssueClusterRequest(
        @NotNull @Range(min = -180, max = 180) Double minLongitude,
        @NotNull @Range(min = -90, max = 90) Double minLatitude,
        @NotNull @Range(min = -180, max = 180) Double maxLongitude,
        @NotNull @Range(min = -90, max = 90) Double maxLatitude,
        @NotNull @Range(min = 0, max = 17) Integer zoomLevel // max zoom 1.2 meters/pixel
// Zoom leve reference:
// https://support.plexearth.com/hc/en-us/articles/6325794324497-Understanding-Zoom-Level-in-Maps-and-Imagery
) {

    @AssertTrue(message = "Minimum latitude must be less than Maximum latitude")
    @JsonIgnore
    public boolean isLatitudeValid() {
        return minLatitude == null || maxLatitude == null || minLatitude < maxLatitude;
    }

    @AssertTrue(message = "Minimum longitude must be less than Maximum longitude")
    @JsonIgnore
    public boolean isLongitudeValid() {
        return minLongitude == null || maxLongitude == null || minLongitude < maxLongitude;
    }
}
