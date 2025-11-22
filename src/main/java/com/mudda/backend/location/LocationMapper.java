package com.mudda.backend.location;

import org.locationtech.jts.geom.Coordinate;

/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File : LocationMapper
 * Author : Vikas Kumar
 * Created : 12-11-2025
 * ---------------------------------------------------------------
 */
public class LocationMapper {

    public static LocationDTO toSummary(Location location) {
        return new LocationDTO(
                location.getCity(),
                location.getState());
    }

    public static LocationResponse toResponse(Location location) {
        return new LocationResponse(
                location.getLocationId(),
                location.getAddressLine(),
                location.getPinCode(),
                location.getCity(),
                location.getState(),
                location.getCreatedAt(),
                CoordinateDTO.from(location.getCoordinate())
            );
    }

    public static Location toLocation(CreateLocationRequest locationRequest) {
        Coordinate coordinate = new Coordinate(locationRequest.coordinate().x(),
                locationRequest.coordinate().y());
        return new Location(locationRequest.addressLine(),
                locationRequest.pinCode(),
                locationRequest.city(),
                locationRequest.state(),
                PointFactory.createPoint(coordinate));
    }
}
