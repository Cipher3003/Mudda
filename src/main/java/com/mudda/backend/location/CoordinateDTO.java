package com.mudda.backend.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.locationtech.jts.geom.Point;

public record CoordinateDTO(
        @JsonProperty("x") double x,
        @JsonProperty("y") double y
) {
    public static CoordinateDTO from(Point point) {
        return new CoordinateDTO(point.getX(), point.getY());
    }
}
