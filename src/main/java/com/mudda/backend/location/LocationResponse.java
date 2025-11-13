package com.mudda.backend.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.locationtech.jts.geom.Point;

import java.time.Instant;

/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : LocationResponse
 * Author  : Vikas Kumar
 * Created : 12-11-2025
 * ---------------------------------------------------------------
 */
public record LocationResponse(
        Long id,
        @JsonProperty("address_line") String addressLine,
        @JsonProperty("pin_code") String pinCode,
        String city,
        String state,
        @JsonProperty("created_at") Instant createdAt,
        Point coordinate
) {
}
