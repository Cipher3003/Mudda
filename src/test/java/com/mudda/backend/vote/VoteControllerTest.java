package com.mudda.backend.vote;

import com.mudda.backend.AbstractIntegrationTest;
import com.mudda.backend.TestDataFactory;
import com.mudda.backend.issue.Issue;
import com.mudda.backend.issue.IssueMapper;
import com.mudda.backend.issue.IssueRepository;
import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.UserMapper;
import com.mudda.backend.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link VoteController}.
 *
 * <p>
 * Each test is fully isolated via {@code @Transactional} — data seeded inside a
 * test method is rolled back automatically after the test completes. No shared
 * state or ordered test execution is required.
 * </p>
 *
 * <p>
 * Every test case provisions its own user, location, category, and issue so
 * that vote state is always predictable (clean slate: no prior votes on the
 * issue).
 * </p>
 */
@Transactional
class VoteControllerTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IssueRepository issueRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String USERNAME = "vote_test_user";
    private static final String EMAIL = "vote_test@test.com";
    private static final String PASSWORD = "Password123!";

    private MuddaUser seedUser() {
        MuddaUser user = UserMapper.toMuddaUser(
                TestDataFactory.validRegisterRequest(USERNAME, EMAIL, passwordEncoder.encode(PASSWORD))
        );
        user.setEnabled(true);
        return userRepository.save(user);
    }

    private Issue seedIssue(long userId) {
        return issueRepository.save(IssueMapper.toIssue(userId, TestDataFactory.validIssueRequest()));
    }

    // region POST

    @Test
    @DisplayName("POST /api/v1/issues/{id}/votes → 200 OK, has_user_voted=true (authenticated)")
    void castVote_authenticated_shouldReturn200WithVotedTrue() throws Exception {
        MuddaUser user = seedUser();
        Issue issue = seedIssue(user.getUserId());
        String token = loginAndGetToken(USERNAME, PASSWORD);

        mockMvc.perform(post("/api/v1/issues/%d/votes".formatted(issue.getId()))
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasVoted").value(true))
                .andExpect(jsonPath("$.voteCount").isNumber());
    }

    @Test
    @DisplayName("POST /api/v1/issues/{id}/votes → 401 Unauthorized when no token provided")
    void castVote_unauthenticated_shouldReturn401() throws Exception {
        MuddaUser user = seedUser();
        Issue issue = seedIssue(user.getUserId());

        mockMvc.perform(post("/api/v1/issues/%d/votes".formatted(issue.getId()))
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/issues/{id}/votes → vote_count increases by 1 after casting")
    void castVote_authenticated_shouldIncrementVoteCount() throws Exception {
        MuddaUser user = seedUser();
        Issue issue = seedIssue(user.getUserId());
        String token = loginAndGetToken(USERNAME, PASSWORD);

        mockMvc.perform(post("/api/v1/issues/%d/votes".formatted(issue.getId()))
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.voteCount").isNumber())
                .andExpect(jsonPath("$.voteCount").value(1));
    }

    @Test
    @DisplayName("POST /api/v1/issues/999999/votes → 404 Not Found for non-existent issue")
    void castVote_nonExistentIssue_shouldReturn404() throws Exception {
        seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);

        mockMvc.perform(post("/api/v1/issues/999999/votes")
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    // endregion

    // region DELETE

    @Test
    @DisplayName("DELETE /api/v1/issues/{id}/votes → 200 OK, has_user_voted=false after removing vote")
    void removeVote_authenticated_shouldReturn200WithVotedFalse() throws Exception {
        MuddaUser user = seedUser();
        Issue issue = seedIssue(user.getUserId());
        String token = loginAndGetToken(USERNAME, PASSWORD);

        mockMvc.perform(post("/api/v1/issues/%d/votes".formatted(issue.getId()))
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasVoted").value(true));

        mockMvc.perform(delete("/api/v1/issues/%d/votes".formatted(issue.getId()))
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasVoted").value(false))
                .andExpect(jsonPath("$.voteCount").isNumber());
    }

    @Test
    @DisplayName("DELETE /api/v1/issues/{id}/votes → 401 Unauthorized when no token provided")
    void removeVote_unauthenticated_shouldReturn401() throws Exception {
        MuddaUser user = seedUser();
        Issue issue = seedIssue(user.getUserId());

        mockMvc.perform(delete("/api/v1/issues/%d/votes".formatted(issue.getId()))
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /api/v1/issues/999999/votes → 404 Not Found for non-existent issue")
    void removeVote_nonExistentIssue_shouldReturn404() throws Exception {
        seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);

        mockMvc.perform(delete("/api/v1/issues/999999/votes")
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    // endregion

    // region GET

    @Test
    @DisplayName("GET /api/v1/votes → 200 OK with paginated vote list (developer endpoint)")
    void getAllVotes_shouldReturn200WithPaginatedVotes() throws Exception {
        mockMvc.perform(get("/api/v1/votes")
                        .header("X-Client-Type", "mobile")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // endregion

    @Test
    @DisplayName("Vote lifecycle: cast then remove → vote_count returns to 0")
    void voteLifecycle_castThenRemove_voteCountReturnsToZero() throws Exception {
        MuddaUser user = seedUser();
        Issue issue = seedIssue(user.getUserId());
        String token = loginAndGetToken(USERNAME, PASSWORD);

        mockMvc.perform(post("/api/v1/issues/%d/votes".formatted(issue.getId()))
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.voteCount").isNumber())
                .andExpect(jsonPath("$.voteCount").value(1));

        mockMvc.perform(delete("/api/v1/issues/%d/votes".formatted(issue.getId()))
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.voteCount").isNumber())
                .andExpect(jsonPath("$.voteCount").value(0));
    }
}
