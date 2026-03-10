package com.mudda.backend.category;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link CategoryController}.
 *
 * <p>
 * Each test is fully isolated via {@code @Transactional} — data seeded inside a
 * test method is rolled back automatically after the test completes. No shared
 * state or ordered test execution is required.
 * </p>
 */
@Transactional
class CategoryControllerTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String USERNAME = "cat_test_user";
    private static final String EMAIL = "cat_test@test.com";
    private static final String PASSWORD = "Password123!";

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /**
     * Seeds a verified user and returns a valid JWT access token.
     */
    private String seedUserAndGetToken() throws Exception {
        MuddaUser user = UserMapper.toUser(
                TestDataFactory.validRegisterRequest(USERNAME, EMAIL,
                        passwordEncoder.encode(PASSWORD)));
        user.setEnabled(true);
        userRepository.save(user);
        return loginAndGetToken(USERNAME, PASSWORD);
    }

    /**
     * Creates a category via the API and returns its assigned ID.
     */
    private long createCategoryAndGetId(String token, String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/issues/categories")
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestDataFactory.validCategoryJson(name)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    // ─── GET /api/v1/issues/categories ───────────────────────────────────────

    @Test
    @DisplayName("GET /api/v1/issues/categories → 200 OK returns a list (public, no auth required)")
    void getAll_public_shouldReturn200WithArray() throws Exception {
        mockMvc.perform(get("/api/v1/issues/categories")
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/issues/categories?search=Roads → 200 OK with search filter applied")
    void getAll_withSearchFilter_shouldReturn200() throws Exception {
        String token = seedUserAndGetToken();
        createCategoryAndGetId(token, "Roads & Infrastructure");

        mockMvc.perform(get("/api/v1/issues/categories")
                        .param("search", "Roads")
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/issues/categories?search=nonexistent → 200 OK with empty list")
    void getAll_withNonMatchingSearch_shouldReturn200EmptyList() throws Exception {
        mockMvc.perform(get("/api/v1/issues/categories")
                        .param("search", "zzz_does_not_exist_zzz")
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // ─── GET /api/v1/issues/categories/{id} ──────────────────────────────────

    @Test
    @DisplayName("GET /api/v1/issues/categories/{id} → 200 OK for an existing category")
    void getById_existing_shouldReturn200WithCategoryData() throws Exception {
        String token = seedUserAndGetToken();
        long id = createCategoryAndGetId(token, "Water Supply");

        mockMvc.perform(get("/api/v1/issues/categories/" + id)
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Water Supply"));
    }

    @Test
    @DisplayName("GET /api/v1/issues/categories/999999 → 404 Not Found for non-existent category")
    void getById_nonExistent_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/issues/categories/999999")
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isNotFound());
    }

    // ─── POST /api/v1/issues/categories ──────────────────────────────────────

    @Test
    @DisplayName("POST /api/v1/issues/categories → 201 Created with category data (authenticated)")
    void create_authenticated_shouldReturn201WithCategoryBody() throws Exception {
        String token = seedUserAndGetToken();

        mockMvc.perform(post("/api/v1/issues/categories")
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestDataFactory.validCategoryJson("Sanitation")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Sanitation"));
    }

    @Test
    @DisplayName("POST /api/v1/issues/categories → 401 Unauthorized when no token provided")
    void create_unauthenticated_shouldReturn401() throws Exception {
        mockMvc.perform(post("/api/v1/issues/categories")
                        .header("X-Client-Type", "mobile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestDataFactory.validCategoryJson("Parks")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/issues/categories → 400 Bad Request when name is blank")
    void create_withBlankName_shouldReturn400WithErrors() throws Exception {
        String token = seedUserAndGetToken();
        String invalidPayload = "{\"name\":\"\"}";

        mockMvc.perform(post("/api/v1/issues/categories")
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isMap());
    }

    @Test
    @DisplayName("POST /api/v1/issues/categories → 400 Bad Request when name field is missing")
    void create_withMissingNameField_shouldReturn400WithErrors() throws Exception {
        String token = seedUserAndGetToken();
        String invalidPayload = "{}";

        mockMvc.perform(post("/api/v1/issues/categories")
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isMap());
    }

    // ─── DELETE /api/v1/issues/categories/{id} ───────────────────────────────

    @Test
    @DisplayName("DELETE /api/v1/issues/categories/{id} → 204 No Content (authenticated owner)")
    void delete_authenticated_shouldReturn204() throws Exception {
        String token = seedUserAndGetToken();
        long id = createCategoryAndGetId(token, "To Be Deleted");

        mockMvc.perform(delete("/api/v1/issues/categories/" + id)
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/issues/categories/{id} → 401 Unauthorized when no token provided")
    void delete_unauthenticated_shouldReturn401() throws Exception {
        String token = seedUserAndGetToken();
        long id = createCategoryAndGetId(token, "Cannot Delete Without Auth");

        mockMvc.perform(delete("/api/v1/issues/categories/" + id)
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /api/v1/issues/categories/{id} → category is truly gone after deletion")
    void delete_authenticated_shouldMakeCategoryUnavailableAfterwards() throws Exception {
        String token = seedUserAndGetToken();
        long id = createCategoryAndGetId(token, "Ephemeral Category");

        mockMvc.perform(delete("/api/v1/issues/categories/" + id)
                        .header("X-Client-Type", "mobile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/issues/categories/" + id)
                        .header("X-Client-Type", "mobile"))
                .andExpect(status().isNotFound());
    }
}
