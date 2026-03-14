package com.mudda.backend;

import com.mudda.backend.user.dto.CreateUserRequest;
import com.mudda.backend.user.MuddaUserRole;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Static factory for request payloads used across integration tests.
 * No Spring context required — plain Java only.
 */
public final class TestDataFactory {

    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    private TestDataFactory() {
    }

    // ─── User ─────────────────────────────────────────────────────────────────

    /**
     * Returns a fully-valid {@link CreateUserRequest} with a unique username/email
     * derived from the supplied suffix. Call with different suffixes in different tests
     * to avoid unique-constraint collisions across tests.
     */
    public static CreateUserRequest validRegisterRequest(String username, String email, String password) {
        return new CreateUserRequest(
                username,
                "Test User",
                email,
                LocalDate.of(1995, 6, 15),
                uniquePhoneNumber(),
                password,
                MuddaUserRole.CITIZEN,
                null,
                null
        );
    }

    /**
     * Generates a unique username like {@code testuser_1}, {@code testuser_2}, …
     */
    public static String uniqueUsername() {
        return "testuser_" + COUNTER.getAndIncrement();
    }

    /**
     * Generates a unique email like {@code testuser_1@test.com}.
     */
    public static String uniqueEmail() {
        return "testuser_" + COUNTER.getAndIncrement() + "@test.com";
    }

    /**
     * Generates a unique phoneNumber like {@code +9178451232659}.
     */
    public static String uniquePhoneNumber() {
        return "+91123456789" + COUNTER.getAndIncrement();
    }

    // ─── Location ─────────────────────────────────────────────────────────────

    /**
     * Returns a JSON string for a valid {@code CreateLocationRequest}.
     * Coordinates are set to a fixed point in New Delhi.
     */
    public static String validLocationJson() {
        return """
                {
                  "address_line": "123 Test Street",
                  "pin_code": "110001",
                  "city": "New Delhi",
                  "state": "Delhi",
                  "coordinate": {
                    "longitude": 77.2090,
                    "latitude": 28.6139
                  }
                }
                """;
    }

    // ─── Category ─────────────────────────────────────────────────────────────

    /**
     * Returns a JSON string for a valid {@code CreateCategoryRequest}.
     */
    public static String validCategoryJson(String name) {
        return "{\"name\":\"" + name + "\"}";
    }

    // ─── Issue ────────────────────────────────────────────────────────────────

    /**
     * Returns a JSON string for a valid {@code CreateIssueRequest}
     * using the supplied {@code locationId} and {@code categoryId}.
     */
    public static String validIssueJson(long locationId, long categoryId) {
        return String.format("""
                {
                  "title": "Broken streetlight on main road",
                  "description": "The streetlight near the main intersection has been broken for 2 weeks.",
                  "location_id": %d,
                  "category_id": %d,
                  "media_urls": []
                }
                """, locationId, categoryId);
    }

    // ─── Comment ──────────────────────────────────────────────────────────────

    /**
     * Returns a JSON string for a valid {@code CreateCommentRequest}.
     */
    public static String validCommentJson() {
        return "{\"text\":\"This is a test comment.\"}";
    }
}
