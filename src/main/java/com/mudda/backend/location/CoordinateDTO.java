package com.mudda.backend.location;

import org.locationtech.jts.geom.Point;

public record CoordinateDTO(
        double x,
        double y
) {
    public static CoordinateDTO from(Point point) {
        return new CoordinateDTO(point.getX(), point.getY());
    }
}
