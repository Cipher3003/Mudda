package com.mudda.backend.issue;

import com.mudda.backend.AbstractIntegrationTest;
import com.mudda.backend.TestDataFactory;
import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.UserMapper;
import com.mudda.backend.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link IssueController}.
 *
 * <p>
 * Each test is fully isolated via {@code @Transactional} — data seeded inside a
 * test method is rolled back automatically after the test completes. No shared
 * state or ordered test execution is required.
 * </p>
 */
@Transactional
class IssueControllerTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IssueRepository issueRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String USERNAME = "issue_test_user";
    private static final String EMAIL = "issue_test@test.com";
    private static final String PASSWORD = "Password123!";

    private MuddaUser seedUser() {
        MuddaUser user = UserMapper.toUser(
                TestDataFactory.validRegisterRequest(USERNAME, EMAIL, passwordEncoder.encode(PASSWORD))
        );
        user.setEnabled(true);
        return userRepository.save(user);
    }

    private Issue seedIssue(long userId) {
        return issueRepository.save(IssueMapper.toIssue(userId, TestDataFactory.validIssueRequest()));
    }

    // region GET

    @Test
    @DisplayName("GET /api/v1/issues → 200 OK with paginated content (public, no auth required)")
    void getAll_public_shouldReturn200WithPaginatedContent() throws Exception {
        mockMvc.perform(get("/api/v1/issues")
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").exists());
    }

    @Test
    @DisplayName("GET /api/v1/issues?page=0&size=5&sortBy=CREATED_AT&sortOrder=desc → 200 OK with correct page size")
    void getAll_withPaginationParams_shouldReturn200WithCorrectPageSize() throws Exception {
        mockMvc.perform(get("/api/v1/issues")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "CREATED_AT")
                        .param("sortOrder", "desc")
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size").value(5));
    }

    @Test
    @DisplayName("GET /api/v1/issues?sortBy=INVALID → 400 Bad Request for invalid sort field")
    void getAll_withInvalidSortBy_shouldReturn400() throws Exception {
        mockMvc.perform(get("/api/v1/issues")
                        .param("sortBy", "INVALID_FIELD")
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/issues/dashboard → 200 OK (public, no auth required)")
    void getDashboard_public_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/issues/dashboard")
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/issues/clusters → 200 OK with cluster data (public)")
    void getClusters_public_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/issues/clusters")
                        .param("minLatitude", "28.0")
                        .param("maxLatitude", "29.0")
                        .param("minLongitude", "77.0")
                        .param("maxLongitude", "78.0")
                        .param("zoomLevel", "1")
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/issues/{id} → 200 OK for existing issue (public)")
    void getById_existing_shouldReturn200WithIssueData() throws Exception {
        MuddaUser user = seedUser();
        Issue issue = seedIssue(user.getUserId());

        mockMvc.perform(get("/api/v1/issues/%d".formatted(issue.getId()))
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(issue.getId()))
                .andExpect(jsonPath("$.title").value(issue.getTitle()));
    }

    @Test
    @DisplayName("GET /api/v1/issues/999999 → 404 Not Found for non-existent issue")
    void getById_nonExistent_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/issues/999999")
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isNotFound());
    }

    // endregion

    // region POST

    @Test
    @DisplayName("POST /api/v1/issues → 201 Created with correct body (authenticated)")
    void create_authenticated_shouldReturn201WithIssueBody() throws Exception {
        seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);

        String payload = TestDataFactory.validIssueJson();

        mockMvc.perform(post("/api/v1/issues")
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Broken streetlight on main road"))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    @DisplayName("POST /api/v1/issues → 401 Unauthorized when no token provided")
    void create_unauthenticated_shouldReturn401() throws Exception {
        seedUser();

        String payload = TestDataFactory.validIssueJson();

        mockMvc.perform(post("/api/v1/issues")
                        .header("X-Client-Type", "mobile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/issues → 400 Bad Request when title is blank")
    void create_withBlankTitle_shouldReturn400WithErrors() throws Exception {
        seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);

        String invalidPayload = TestDataFactory.issueRequestJson("", null);

        mockMvc.perform(post("/api/v1/issues")
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isMap());
    }

    @Test
    @DisplayName("POST /api/v1/issues → 400 Bad Request when location_id is missing")
    void create_withMissingLocationId_shouldReturn400WithErrors() throws Exception {
        seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);

        String invalidPayload = TestDataFactory.issueRequestJson("", null);

        mockMvc.perform(post("/api/v1/issues")
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isMap());
    }

    @Test
    @DisplayName("POST /api/v1/issues → 400 Bad Request when media_urls exceeds max size of 5")
    void create_withTooManyMediaUrls_shouldReturn400() throws Exception {
        seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);

        String invalidPayload = TestDataFactory.issueRequestJson(
                "Title", List.of("u1", "u2", "u3", "u4", "u5", "u6")
        );

        mockMvc.perform(post("/api/v1/issues")
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest());
    }

    // endregion

    // region PUT

    @Test
    @DisplayName("PUT /api/v1/issues/{id} → 200 OK with updated title (owner)")
    void update_owner_shouldReturn200WithUpdatedTitle() throws Exception {
        MuddaUser user = seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);
        Issue issue = seedIssue(user.getUserId());

        String payload = TestDataFactory.validIssueUpdateRequestJson();

        mockMvc.perform(put("/api/v1/issues/%d".formatted(issue.getId()))
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"));
    }

    @Test
    @DisplayName("PUT /api/v1/issues/{id} → 400 Bad Request")
    void update_invalidRequest_shouldReturn400() throws Exception {
        MuddaUser user = seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);
        Issue issue = seedIssue(user.getUserId());

        String invalidPayload = """
                {
                  "title": "Updated Title",
                  "description": "Updated description",
                  "status": "IN_PROGRESS"
                }
                """;

        mockMvc.perform(put("/api/v1/issues/%d".formatted(issue.getId()))
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("PUT /api/v1/issues/{id} → 401 Unauthorized when no token provided")
    void update_unauthenticated_shouldReturn401() throws Exception {
        MuddaUser user = seedUser();
        Issue issue = seedIssue(user.getUserId());

        String payload = TestDataFactory.validIssueUpdateRequestJson();

        mockMvc.perform(put("/api/v1/issues/%d".formatted(issue.getId()))
                        .header("X-Client-Type", "mobile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /api/v1/issues/999999 → 404 Not Found when issue does not exist")
    void update_nonExistent_shouldReturn404() throws Exception {
        seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);

        String payload = TestDataFactory.validIssueUpdateRequestJson();

        mockMvc.perform(put("/api/v1/issues/999999")
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound());
    }

    // endregion

    // region DELETE

    @Test
    @DisplayName("DELETE /api/v1/issues/{id} → 204 No Content (owner)")
    void delete_owner_shouldReturn204() throws Exception {
        MuddaUser user = seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);
        Issue issue = seedIssue(user.getUserId());

        mockMvc.perform(delete("/api/v1/issues/%d".formatted(issue.getId()))
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/issues/{id} → 401 Unauthorized when no token provided")
    void delete_unauthenticated_shouldReturn401() throws Exception {
        MuddaUser user = seedUser();
        Issue issue = seedIssue(user.getUserId());

        mockMvc.perform(delete("/api/v1/issues/%d".formatted(issue.getId()))
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /api/v1/issues/{id} → issue is truly gone after deletion")
    void delete_owner_shouldMakeIssueUnavailableAfterwards() throws Exception {
        MuddaUser user = seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);
        Issue issue = seedIssue(user.getUserId());

        mockMvc.perform(delete("/api/v1/issues/%d".formatted(issue.getId()))
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/issues/%d".formatted(issue.getId()))
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/issues/999999 → 404 Not Found when issue does not exist")
    void delete_nonExistent_shouldReturn404() throws Exception {
        seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);

        mockMvc.perform(delete("/api/v1/issues/999999")
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    // endregion

}
