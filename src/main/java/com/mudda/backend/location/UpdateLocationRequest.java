package com.mudda.backend.location;

public record UpdateLocationRequest(
        String addressLine,
        String pinCode
) {
}
