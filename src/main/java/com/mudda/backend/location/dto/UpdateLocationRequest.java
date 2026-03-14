package com.mudda.backend.location.dto;

public record UpdateLocationRequest(
        String addressLine,
        String pinCode,
        String city,
        String state
) {
}
