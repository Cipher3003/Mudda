/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : LocationResponse
 * Author  : Vikas Kumar
 * Created : 12-11-2025
 * ---------------------------------------------------------------
 */
package com.mudda.backend.location.dto;

import java.time.Instant;

public record LocationResponse(
        Long id,
        String addressLine,
        String pinCode,
        String city,
        String state,
        Instant createdAt,
        CoordinateDTO coordinate
) {
}
