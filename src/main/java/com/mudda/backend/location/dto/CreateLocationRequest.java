package com.mudda.backend.location.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record CreateLocationRequest(
        @NotNull @NotBlank String addressLine,
        @NotNull @NotBlank String pinCode,
        @NotNull @NotBlank String city,
        @NotNull @NotBlank String state,
        @NotNull CoordinateDTO coordinate
) {
}
