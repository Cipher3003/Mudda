package com.mudda.backend.location;

public record LocationResponse(
        Long locationId,
        String addressLine,
        String city,
        String state,
        String pinCode,
        CoordinateDTO coordinate
) {
    public static LocationResponse from(Location location, Locality locality) {
        return new LocationResponse(
                location.getLocationId(),
                location.getAddressLine(),
                locality.getCity(),
                locality.getState(),
                location.getPinCode(),
                CoordinateDTO.from(location.getCoordinate())
        );
    }
}
