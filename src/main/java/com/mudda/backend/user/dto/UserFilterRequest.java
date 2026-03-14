/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : UserFilterRequest
 * Author  : Vikas Kumar
 * Created : 13-11-2025
 * ---------------------------------------------------------------
 */
package com.mudda.backend.user.dto;

import com.mudda.backend.user.MuddaUserRole;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

public record UserFilterRequest(
        String name,
        MuddaUserRole role,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant createdAfter,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant createdBefore
) {
}
