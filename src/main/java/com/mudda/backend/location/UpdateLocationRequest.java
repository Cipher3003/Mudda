package com.mudda.backend.location;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateLocationRequest(
        @JsonProperty("address_line") String addressLine,
        @JsonProperty("pin_code") String pinCode,
        String city,
        String state
) {
}
