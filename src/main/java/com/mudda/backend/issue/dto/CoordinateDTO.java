package com.mudda.backend.issue.dto;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CoordinateDTO(
        @JsonProperty("longitude") Double x,
        @JsonProperty("latitude") Double y
) {
    public static CoordinateDTO from(Point point) {
        return new CoordinateDTO(point.getX(), point.getY());
    }
}
