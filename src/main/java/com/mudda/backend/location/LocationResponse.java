package com.mudda.backend.location;

public record LocationResponse(
        Long locationId,
        String addressLine,
        String city,
        String state,
        String pinCode,
        CoordinateDTO coordinate
) {
    public static LocationResponse from(Location location) {
        return new LocationResponse(
                location.getLocationId(),
                location.getAddressLine(),
                location.getCity(),
                location.getState(),
                location.getPinCode(),
                CoordinateDTO.from(location.getCoordinate())
        );
    }
}
