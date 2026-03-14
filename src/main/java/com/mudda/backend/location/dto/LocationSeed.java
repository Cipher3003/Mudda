/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : LocationSeed
 * Author  : Vikas Kumar
 * Created : 28-11-2025
 * ---------------------------------------------------------------
 */
package com.mudda.backend.location.dto;

import com.mudda.backend.location.Location;

public record LocationSeed(
        String addressLine,
        String pinCode,
        String city,
        String state,
        CoordinateSeed coordinate
) {

    public static Location toLocation(LocationSeed seed) {
        return new Location(
                seed.addressLine(),
                seed.pinCode(),
                seed.city(),
                seed.state(),
                CoordinateSeed.toPoint(seed.coordinate())
        );
    }
}
