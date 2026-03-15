package com.mudda.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mudda.backend.comment.dto.CreateCommentRequest;
import com.mudda.backend.issue.IssueCategory;
import com.mudda.backend.issue.IssueStatus;
import com.mudda.backend.issue.dto.CreateIssueRequest;
import com.mudda.backend.issue.dto.UpdateIssueRequest;
import com.mudda.backend.issue.dto.CoordinateDTO;
import com.mudda.backend.user.MuddaUserRole;
import com.mudda.backend.user.dto.CreateUserRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Static factory for request payloads used across integration tests.
 * No Spring context required — plain Java only.
 */
public final class TestDataFactory {

    private static final AtomicInteger COUNTER = new AtomicInteger(1);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private TestDataFactory() {
    }

    // region User

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
     * Generates a unique phoneNumber like {@code +9178451232659}.
     */
    public static String uniquePhoneNumber() {
        return "+91123456789" + COUNTER.getAndIncrement();
    }

    // endregion

    // region Issue

    /**
     * Returns a JSON string for a valid {@code CreateIssueRequest}
     * using the supplied {@code locationId} and {@code category}.
     */
    public static String validIssueJson() throws JsonProcessingException {
        return MAPPER.writeValueAsString(validIssueRequest());
    }

    public static CreateIssueRequest validIssueRequest() {
        return new CreateIssueRequest(
                "Broken streetlight on main road",
                "The streetlight near the main intersection has been broken for 2 weeks.",
                IssueCategory.ELE.getCode(),
                null,
                "122333", "City", "State", new CoordinateDTO(90.0, 90.0)
        );
    }

    public static String issueRequestJson(String title, List<String> mediaUrls) throws JsonProcessingException {
        CreateIssueRequest request = new CreateIssueRequest(
                title,
                "The streetlight near the main intersection has been broken for 2 weeks.",
                IssueCategory.ELE.getCode(),
                mediaUrls,
                "122333", "City", "State", new CoordinateDTO(90.0, 90.0)
        );
        return MAPPER.writeValueAsString(request);
    }

    public static String validIssueUpdateRequestJson()
            throws JsonProcessingException {
        UpdateIssueRequest request = new UpdateIssueRequest(
                "New Title",
                "The streetlight near the main intersection has been broken for 2 weeks.",
                IssueStatus.PENDING.name()
        );
        return MAPPER.writeValueAsString(request);
    }

    // endregion

    // region Comment

    /**
     * Returns a JSON string for a valid {@code CreateCommentRequest}.
     */
    public static String validCommentJson() throws JsonProcessingException {
        CreateCommentRequest request = new CreateCommentRequest("This is a test comment.");
        return MAPPER.writeValueAsString(request);
    }

    public static String commentJson(String title) throws JsonProcessingException {
        CreateCommentRequest request = new CreateCommentRequest(title);
        return MAPPER.writeValueAsString(request);
    }

    // endregion
}
