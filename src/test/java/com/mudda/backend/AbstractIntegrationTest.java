package com.mudda.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mudda.backend.media.MediaService;
import com.mudda.backend.email.EmailService;
import com.mudda.backend.notification.FirebaseConfig;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Base class for all integration tests.
 * <p>
 * External services (Email, S3) are replaced with @MockBean so tests run
 * completely offline and deterministically.
 * </p>
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    // ─── Shared MockMvc & Jackson ─────────────────────────────────────────────

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    // ─── Mocked external services ─────────────────────────────────────────────

    /**
     * Prevents real email sends during tests.
     */
    @MockBean
    protected EmailService emailService;

    /**
     * Prevents real AWS S3 calls during tests.
     */
    @MockBean
    protected MediaService mediaService;

    @MockBean
    protected FirebaseConfig firebaseConfig;

    @MockBean
    protected SqsAsyncClient sqsAsyncClient;

    @MockBean
    protected SqsTemplate sqsTemplate;

    // ─── Auth helpers ────────────────────────────────────────────────────────

    /**
     * Registers a user (ignores response body — email verification is mocked),
     * then directly enables the user in the DB, logs in, and returns the JWT
     * access token.
     *
     * <p>
     * Subclasses that need to test the registration flow itself should call
     * the endpoint directly — this helper is only for <em>obtaining a valid
     * token</em> so other tests can authenticate.
     * </p>
     */
    protected String registerVerifyAndLogin(String username, String email, String password)
            throws Exception {
        // 1. Register
        String registerBody = objectMapper.writeValueAsString(
                TestDataFactory.validRegisterRequest(username, email, password));

        mockMvc.perform(post("/auth/register")
                        .header("X-Client-Type", "mobile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

//        TODO: implement this second step
//        endpoint = /verify-email/confirm?verifyToken=
        // 2. Verify the user directly via the verify-email endpoint
        // We need the token — obtain it from DB via the helper
        return loginAndGetToken(username, password);
    }

    /**
     * Logs in with the given credentials and returns the JWT access token.
     * The user must already be enabled (verified) in the database.
     */
    protected String loginAndGetToken(String username, String password) throws Exception {
        String loginBody = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .header("X-Client-Type", "mobile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return objectMapper.readTree(responseBody).get("accessToken").asText();
    }
}
