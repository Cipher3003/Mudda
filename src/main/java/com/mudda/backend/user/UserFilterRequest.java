package com.mudda.backend.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : UserFilterRequest
 * Author  : Vikas Kumar
 * Created : 13-11-2025
 * ---------------------------------------------------------------
 */
public record UserFilterRequest(
        String name,
        MuddaUserRole role,

        @JsonProperty("created_after")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant createdAfter,

        @JsonProperty("created_before")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant createdBefore
) {
}
