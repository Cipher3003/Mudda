/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : CoordinateSeed
 * Author  : Vikas Kumar
 * Created : 28-11-2025
 * ---------------------------------------------------------------
 */
package com.mudda.backend.location.dto;

import com.mudda.backend.location.PointFactory;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

public record CoordinateSeed(
        double y,
        double x
) {

    public static Point toPoint(CoordinateSeed seed) {
        return PointFactory.createPoint(new Coordinate(seed.x(), seed.y()));
    }
}
