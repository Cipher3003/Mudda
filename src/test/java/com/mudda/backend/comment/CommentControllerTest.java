package com.mudda.backend.comment;

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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link CommentController}.
 *
 * <p>
 * Each test is fully isolated via {@code @Transactional} — data seeded inside a
 * test method is rolled back automatically after the test completes. No shared
 * state or ordered test execution is required.
 * </p>
 *
 * <p>
 * Every test case that needs an issue to comment on provisions its own user,
 * location, category, and issue via private helpers.
 * </p>
 */
@Transactional
class CommentControllerTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private CommentRepository commentRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String USERNAME = "comment_test_user";
    private static final String EMAIL = "comment_test@test.com";
    private static final String PASSWORD = "Password123!";

    private MuddaUser seedUser() {
        MuddaUser user = UserMapper.toMuddaUser(
                TestDataFactory.validRegisterRequest(USERNAME, EMAIL, passwordEncoder.encode(PASSWORD))
        );
        user.setEnabled(true);
        return userRepository.saveAndFlush(user);
    }

    private Issue seedIssue(long userId) {
        return issueRepository.saveAndFlush(IssueMapper.toIssue(userId, TestDataFactory.validIssueRequest()));
    }

    private Comment seedComment(long issueId, long userId) {
        return commentRepository.saveAndFlush(new Comment("Comment", issueId, userId));
    }

    // region GET

    @Test
    @DisplayName("GET /api/v1/issues/{id}/comments → 200 OK with paginated content (public)")
    void getCommentsByIssue_public_shouldReturn200WithPaginatedContent() throws Exception {
        MuddaUser user = seedUser();
        Long issueId = seedIssue(user.getUserId()).getId();

        Comment testComment = seedComment(issueId, user.getUserId());

        mockMvc.perform(get("/api/v1/issues/%d/comments".formatted(issueId))
                        .param("page", "0")
                        .param("size", "20")
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isOk())

                // Assert Spring Page Metadata Properties
                .andExpect(jsonPath("$.page.size").value(20))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(1))

                // Assert Array Elements and Properties inside Content Block
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))

                // Assert the Specific Values of the First Comment Object inside the Array
                .andExpect(jsonPath("$.content[0].id").value(testComment.getId()))
                .andExpect(jsonPath("$.content[0].text").value(testComment.getText()))
                .andExpect(jsonPath("$.content[0].issueId").value(issueId))
                .andExpect(jsonPath("$.content[0].userId").value(user.getUserId()))
                .andExpect(jsonPath("$.content[0].username").value(user.getUsername()))

                // Assert Default Behavior Flags
                .andExpect(jsonPath("$.content[0].hasLiked").value(false))
                .andExpect(jsonPath("$.content[0].canLike").value(false));
    }

    @Test
    @DisplayName("GET /api/v1/issues/999999/comments → 200 OK with empty page for non-existent issueId")
    void getCommentsByIssue_nonExistentIssue_shouldReturn200EmptyPage() throws Exception {
        mockMvc.perform(get("/api/v1/issues/999999/comments")
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/comments/{id} → 200 OK for an existing comment (public)")
    void getCommentById_existing_shouldReturn200WithCommentData() throws Exception {
        MuddaUser user = seedUser();
        Issue issue = seedIssue(user.getUserId());
        Long id = seedComment(user.getUserId(), issue.getId()).getId();

        mockMvc.perform(get("/api/v1/comments/" + id)
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.text").value("Comment"));
    }

    @Test
    @DisplayName("GET /api/v1/comments/999999 → 404 Not Found for non-existent comment")
    void getCommentById_nonExistent_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/comments/999999")
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/comments/{id}/replies → 200 OK with paginated replies (public)")
    void getRepliesByComment_public_shouldReturn200WithPaginatedContent() throws Exception {
        MuddaUser user = seedUser();
        Issue issue = seedIssue(user.getUserId());
        Long id = seedComment(user.getUserId(), issue.getId()).getId();

        mockMvc.perform(get("/api/v1/comments/%d/replies".formatted(id))
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // endregion

    // region POST

    @Test
    @DisplayName("POST /api/v1/comments → 201 Created, returns comment body (authenticated)")
    void createComment_authenticated_shouldReturn201WithCommentBody() throws Exception {
        MuddaUser user = seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);
        Issue issue = seedIssue(user.getUserId());

        String validPayload = TestDataFactory.validCommentJson(issue.getId());

        mockMvc.perform(post("/api/v1/comments")
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").value(notNullValue()))
                .andExpect(jsonPath("$.createdAt").
                        value(matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*Z$")));
    }

    @Test
    @DisplayName("POST /api/v1/comments → 401 Unauthorized when no token provided")
    void createComment_unauthenticated_shouldReturn401() throws Exception {
        MuddaUser user = seedUser();
        Issue issue = seedIssue(user.getUserId());

        String validPayload = TestDataFactory.validCommentJson(issue.getId());

        mockMvc.perform(post("/api/v1/comments")
                        .header("X-Client-Type", "mobile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/comments → 400 Bad Request when text is blank")
    void createComment_withBlankText_shouldReturn400WithErrors() throws Exception {
        MuddaUser user = seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);
        Issue issue = seedIssue(user.getUserId());

        String invalidPayload = TestDataFactory.commentJson("", issue.getId());

        mockMvc.perform(post("/api/v1/comments")
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isMap());
    }

    @Test
    @DisplayName("POST /api/v1/comments → 400 Bad Request when text field is missing")
    void createComment_withMissingTextField_shouldReturn400() throws Exception {
        MuddaUser user = seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);
        Issue issue = seedIssue(user.getUserId());

        String invalidPayload = TestDataFactory.commentJson(null, issue.getId());

        mockMvc.perform(post("/api/v1/comments")
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/comments → 201 Created (authenticated)")
    void createReply_authenticated_shouldReturn201WithReplyBody() throws Exception {
        MuddaUser user = seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);
        Issue issue = seedIssue(user.getUserId());
        Long id = seedComment(user.getUserId(), issue.getId()).getId();

        String payload = TestDataFactory.replyJson("This is a test reply.", issue.getId(), id);

        mockMvc.perform(post("/api/v1/comments")
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").value(notNullValue()))
                .andExpect(jsonPath("$.createdAt").
                        value(matchesPattern("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*Z$")));
    }

    @Test
    @DisplayName("POST /api/v1/comments → 401 Unauthorized when no token provided")
    void createReply_unauthenticated_shouldReturn401() throws Exception {
        MuddaUser user = seedUser();
        Issue issue = seedIssue(user.getUserId());
        Long id = seedComment(user.getUserId(), issue.getId()).getId();

        String payload = TestDataFactory.validReplyJson(issue.getId(), id);

        mockMvc.perform(post("/api/v1/comments")
                        .header("X-Client-Type", "mobile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/comments → 400 Bad Request when text is blank")
    void createReply_withBlankText_shouldReturn400() throws Exception {
        MuddaUser user = seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);
        Issue issue = seedIssue(user.getUserId());
        Long id = seedComment(user.getUserId(), issue.getId()).getId();

        String invalidPayload = TestDataFactory
                .replyJson(null, issue.getId(), id);

        mockMvc.perform(post("/api/v1/comments")
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isMap());
    }

    @Test
    @DisplayName("POST /api/v1/comments/{id}/like → 200 OK, liked=true (authenticated)")
    void likeComment_authenticated_shouldReturn200WithLikedTrue() throws Exception {
        MuddaUser user = seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);
        Issue issue = seedIssue(user.getUserId());
        Long id = seedComment(user.getUserId(), issue.getId()).getId();

        mockMvc.perform(post("/api/v1/comments/%d/like".formatted(id))
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(true))
                .andExpect(jsonPath("$.likeCount").isNumber());
    }

    @Test
    @DisplayName("POST /api/v1/comments/{id}/like → 401 Unauthorized when no token provided")
    void likeComment_unauthenticated_shouldReturn401() throws Exception {
        MuddaUser user = seedUser();
        Issue issue = seedIssue(user.getUserId());
        Long id = seedComment(user.getUserId(), issue.getId()).getId();

        mockMvc.perform(post("/api/v1/comments/%d/like".formatted(id))
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isUnauthorized());
    }

    // endregion

    // region DELETE

    @Test
    @DisplayName("DELETE /api/v1/comments/{id}/like → 200 OK, liked=false after unliking (authenticated)")
    void unlikeComment_authenticated_shouldReturn200WithLikedFalse() throws Exception {
        MuddaUser user = seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);
        Issue issue = seedIssue(user.getUserId());
        Long id = seedComment(user.getUserId(), issue.getId()).getId();

        mockMvc.perform(post("/api/v1/comments/%d/like".formatted(id))
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(true));

        mockMvc.perform(delete("/api/v1/comments/%d/like".formatted(id))
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(false))
                .andExpect(jsonPath("$.likeCount").isNumber());
    }

    @Test
    @DisplayName("DELETE /api/v1/comments/{id}/like → 401 Unauthorized when no token provided")
    void unlikeComment_unauthenticated_shouldReturn401() throws Exception {
        MuddaUser user = seedUser();
        Issue issue = seedIssue(user.getUserId());
        Long id = seedComment(user.getUserId(), issue.getId()).getId();

        mockMvc.perform(delete("/api/v1/comments/%d/like".formatted(id))
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /api/v1/comments/{id} → 204 No Content (authenticated)")
    void deleteComment_authenticated_shouldReturn204() throws Exception {
        MuddaUser user = seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);
        Issue issue = seedIssue(user.getUserId());
        Long id = seedComment(user.getUserId(), issue.getId()).getId();

        mockMvc.perform(delete("/api/v1/comments/%d".formatted(id))
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/comments/{id} → 401 Unauthorized when no token provided")
    void deleteComment_unauthenticated_shouldReturn401() throws Exception {
        MuddaUser user = seedUser();
        Issue issue = seedIssue(user.getUserId());
        Long id = seedComment(user.getUserId(), issue.getId()).getId();

        mockMvc.perform(delete("/api/v1/comments/%d".formatted(id))
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /api/v1/comments/{id} → comment is truly gone (404) after deletion")
    void deleteComment_authenticated_shouldMakeCommentUnavailableAfterwards() throws Exception {
        MuddaUser user = seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);
        Issue issue = seedIssue(user.getUserId());
        Long id = seedComment(user.getUserId(), issue.getId()).getId();

        mockMvc.perform(delete("/api/v1/comments/%d".formatted(id))
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/comments/%d".formatted(id))
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isNotFound());
    }

    // endregion
}
