package com.mudda.backend.location;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class PointFactory {

    private final static GeometryFactory geometryFactory = new GeometryFactory();

    public static Point createPoint(Coordinate coordinate) {
        return geometryFactory.createPoint(coordinate);
    }
}
