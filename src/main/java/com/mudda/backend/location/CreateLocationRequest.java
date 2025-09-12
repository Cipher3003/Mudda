package com.mudda.backend.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;


public record CreateLocationRequest(
        @JsonProperty("address_line") @NotNull String addressLine,
        @JsonProperty("pin_code") @NotNull String pinCode,
        @JsonProperty("city") @NotNull String city,
        @JsonProperty("state") @NotNull String state,
        @JsonProperty("coordinate") @NotNull CoordinateDTO coordinate
) {
}
