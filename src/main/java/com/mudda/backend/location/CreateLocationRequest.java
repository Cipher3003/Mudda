package com.mudda.backend.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record CreateLocationRequest(
        @JsonProperty("address_line") @NotNull @NotBlank String addressLine,
        @JsonProperty("pin_code") @NotNull @NotBlank String pinCode,
        @NotNull @NotBlank String city,
        @NotNull @NotBlank String state,
        @NotNull CoordinateDTO coordinate
) {
}
