package com.mudda.backend.comment;

import com.mudda.backend.AbstractIntegrationTest;
import com.mudda.backend.TestDataFactory;
import com.mudda.backend.category.Category;
import com.mudda.backend.category.CategoryRepository;
import com.mudda.backend.issue.Issue;
import com.mudda.backend.issue.IssueRepository;
import com.mudda.backend.location.Location;
import com.mudda.backend.location.LocationRepository;
import com.mudda.backend.location.PointFactory;
import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.UserMapper;
import com.mudda.backend.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private LocationRepository locationRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CommentRepository commentRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String USERNAME = "comment_test_user";
    private static final String EMAIL = "comment_test@test.com";
    private static final String PASSWORD = "Password123!";

    private MuddaUser seedUser() {
        MuddaUser user = UserMapper.toUser(
                TestDataFactory.validRegisterRequest(USERNAME, EMAIL, passwordEncoder.encode(PASSWORD)));
        user.setEnabled(true);
        return userRepository.save(user);
    }

    private Issue seedIssue(long userId) {
        Location location = locationRepository.save(
                new Location("Address", "122333", "City", "State",
                        PointFactory.createPoint(new Coordinate(90.0, 90.0)))
        );

        Category category = categoryRepository.save(new Category("Category"));

        return issueRepository.save(new Issue("Title", "Description", userId,
                location.getLocationId(), category.getId(), null));
    }

    // region GET

    @Test
    @DisplayName("GET /api/v1/issues/{id}/comments → 200 OK with paginated content (public)")
    void getCommentsByIssue_public_shouldReturn200WithPaginatedContent() throws Exception {
        MuddaUser user = seedUser();
        Long issueId = seedIssue(user.getUserId()).getId();
        mockMvc.perform(get("/api/v1/issues/%d/comments".formatted(issueId))
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
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
        Long id = commentRepository.save(new Comment("Comment", issue.getId(), issue.getUserId())).getCommentId();

        mockMvc.perform(get("/api/v1/comments/" + id)
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment_id").value(id))
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
        Long id = commentRepository.save(new Comment("Comment", issue.getId(), issue.getUserId())).getCommentId();

        mockMvc.perform(get("/api/v1/comments/%d/replies".formatted(id))
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // endregion

    // region POST

    @Test
    @DisplayName("POST /api/v1/issues/{id}/comments → 201 Created, returns comment body (authenticated)")
    void createComment_authenticated_shouldReturn201WithCommentBody() throws Exception {
        MuddaUser user = seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);
        Issue issue = seedIssue(user.getUserId());

        mockMvc.perform(post("/api/v1/issues/%d/comments".formatted(issue.getId()))
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestDataFactory.validCommentJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comment_id").exists())
                .andExpect(jsonPath("$.text").value("This is a test comment."));
    }

    @Test
    @DisplayName("POST /api/v1/issues/{id}/comments → 401 Unauthorized when no token provided")
    void createComment_unauthenticated_shouldReturn401() throws Exception {
        MuddaUser user = seedUser();
        Issue issue = seedIssue(user.getUserId());

        mockMvc.perform(post("/api/v1/issues/%d/comments".formatted(issue.getId()))
                        .header("X-Client-Type", "mobile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestDataFactory.validCommentJson()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/issues/{id}/comments → 400 Bad Request when text is blank")
    void createComment_withBlankText_shouldReturn400WithErrors() throws Exception {
        MuddaUser user = seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);
        Issue issue = seedIssue(user.getUserId());

        String invalidPayload = "{\"text\":\"\"}";

        mockMvc.perform(post("/api/v1/issues/%d/comments".formatted(issue.getId()))
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isMap());
    }

    @Test
    @DisplayName("POST /api/v1/issues/{id}/comments → 400 Bad Request when text field is missing")
    void createComment_withMissingTextField_shouldReturn400() throws Exception {
        MuddaUser user = seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);
        Issue issue = seedIssue(user.getUserId());

        mockMvc.perform(post("/api/v1/issues/%d/comments".formatted(issue.getId()))
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/comments/{id}/replies → 201 Created (authenticated)")
    void createReply_authenticated_shouldReturn201WithReplyBody() throws Exception {
        MuddaUser user = seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);
        Issue issue = seedIssue(user.getUserId());
        Long id = commentRepository.save(new Comment("Comment", issue.getId(), issue.getUserId())).getCommentId();

        mockMvc.perform(post("/api/v1/comments/%d/replies".formatted(id))
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"This is a test reply.\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comment_id").exists())
                .andExpect(jsonPath("$.text").value("This is a test reply."));
    }

    @Test
    @DisplayName("POST /api/v1/comments/{id}/replies → 401 Unauthorized when no token provided")
    void createReply_unauthenticated_shouldReturn401() throws Exception {
        MuddaUser user = seedUser();
        Issue issue = seedIssue(user.getUserId());
        Long id = commentRepository.save(new Comment("Comment", issue.getId(), issue.getUserId())).getCommentId();

        mockMvc.perform(post("/api/v1/comments/%d/replies".formatted(id))
                        .header("X-Client-Type", "mobile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Unauthorized reply.\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/comments/{id}/replies → 400 Bad Request when text is blank")
    void createReply_withBlankText_shouldReturn400() throws Exception {
        MuddaUser user = seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);
        Issue issue = seedIssue(user.getUserId());
        Long id = commentRepository.save(new Comment("Comment", issue.getId(), issue.getUserId())).getCommentId();

        mockMvc.perform(post("/api/v1/comments/%d/replies".formatted(id))
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isMap());
    }

    @Test
    @DisplayName("POST /api/v1/comments/{id}/like → 200 OK, liked=true (authenticated)")
    void likeComment_authenticated_shouldReturn200WithLikedTrue() throws Exception {
        MuddaUser user = seedUser();
        String token = loginAndGetToken(USERNAME, PASSWORD);
        Issue issue = seedIssue(user.getUserId());
        Long id = commentRepository.save(new Comment("Comment", issue.getId(), issue.getUserId())).getCommentId();

        mockMvc.perform(post("/api/v1/comments/%d/like".formatted(id))
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(true))
                .andExpect(jsonPath("$.like_count").isNumber());
    }

    @Test
    @DisplayName("POST /api/v1/comments/{id}/like → 401 Unauthorized when no token provided")
    void likeComment_unauthenticated_shouldReturn401() throws Exception {
        MuddaUser user = seedUser();
        Issue issue = seedIssue(user.getUserId());
        Long id = commentRepository.save(new Comment("Comment", issue.getId(), issue.getUserId())).getCommentId();

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
        Long id = commentRepository.save(new Comment("Comment", issue.getId(), issue.getUserId())).getCommentId();

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
                .andExpect(jsonPath("$.like_count").isNumber());
    }

    @Test
    @DisplayName("DELETE /api/v1/comments/{id}/like → 401 Unauthorized when no token provided")
    void unlikeComment_unauthenticated_shouldReturn401() throws Exception {
        MuddaUser user = seedUser();
        Issue issue = seedIssue(user.getUserId());
        Long id = commentRepository.save(new Comment("Comment", issue.getId(), issue.getUserId())).getCommentId();

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
        Long id = commentRepository.save(new Comment("Comment", issue.getId(), issue.getUserId())).getCommentId();

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
        Long id = commentRepository.save(new Comment("Comment", issue.getId(), issue.getUserId())).getCommentId();

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
        Long id = commentRepository.save(new Comment("Comment", issue.getId(), issue.getUserId())).getCommentId();

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
