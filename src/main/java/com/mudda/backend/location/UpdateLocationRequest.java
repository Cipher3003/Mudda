package com.mudda.backend.location;

public record UpdateLocationRequest(
        String addressLine,
        String pinCode,
        String city,
        String state
) {
}
