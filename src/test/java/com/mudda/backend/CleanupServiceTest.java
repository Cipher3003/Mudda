/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : CleanupServiceTest
 * Author  : Vikas Kumar
 * Created : 6/3/2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend;

import com.mudda.backend.media.Media;
import com.mudda.backend.media.MediaRepository;
import com.mudda.backend.token.refresh.RefreshTokenRepository;
import com.mudda.backend.token.verification.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.time.Instant;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CleanupServiceTest {

    @Mock
    private S3Client s3Client;
    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private VerificationTokenRepository verificationTokenRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private CleanupService cleanupService;

    @BeforeEach
    void setUp() {
        cleanupService = new CleanupService(mediaRepository, s3Client,
                verificationTokenRepository, refreshTokenRepository);
        ReflectionTestUtils.setField(cleanupService, "bucketName", "test-bucket");
    }

    // #region cleanupFailedUploads

    @Test
    @SuppressWarnings("unchecked")
    void shouldDeleteExpiredMediaFromS3AndRepository() {
        Media media = mock(Media.class);
        lenient().when(media.getMediaKey()).thenReturn("images/abc123.jpg");
        when(mediaRepository.findExpiredOrFailed(any(Instant.class))).thenReturn(Stream.of(media));

        cleanupService.cleanupFailedUploads();

        verify(s3Client).deleteObject(any(Consumer.class));
        verify(mediaRepository).delete(media);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldContinueCleanupWhenOneS3DeletionFails() {
        Media failingMedia = mock(Media.class);
        Media successMedia = mock(Media.class);

        lenient().when(failingMedia.getMediaKey()).thenReturn("images/fail.jpg");
        lenient().when(successMedia.getMediaKey()).thenReturn("images/success.jpg");

        when(mediaRepository.findExpiredOrFailed(any(Instant.class)))
                .thenReturn(Stream.of(failingMedia, successMedia));

        when(s3Client.deleteObject(any(Consumer.class)))
                .thenThrow(S3Exception.builder().message("S3 error").statusCode(500).build())
                .thenReturn(DeleteObjectResponse.builder().build());

        cleanupService.cleanupFailedUploads();

        verify(mediaRepository, never()).delete(failingMedia);
        verify(mediaRepository).delete(successMedia);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldDoNothingWhenNoExpiredMediaExists() {
        when(mediaRepository.findExpiredOrFailed(any(Instant.class))).thenReturn(Stream.empty());

        cleanupService.cleanupFailedUploads();

        verify(s3Client, never()).deleteObject(any(Consumer.class));
        verify(mediaRepository, never()).delete(any());
    }

    // #endregion

    // #region cleanupExpiredVerificationTokens

    @Test
    void shouldDeleteExpiredVerificationTokens() {
        when(verificationTokenRepository.deleteAllExpiredToken(any(Instant.class))).thenReturn(5);

        cleanupService.cleanupExpiredVerificationTokens();

        verify(verificationTokenRepository).deleteAllExpiredToken(any(Instant.class));
    }

    @Test
    void shouldHandleNoExpiredVerificationTokens() {
        when(verificationTokenRepository.deleteAllExpiredToken(any(Instant.class))).thenReturn(0);

        cleanupService.cleanupExpiredVerificationTokens();

        verify(verificationTokenRepository).deleteAllExpiredToken(any(Instant.class));
    }

    // #endregion

    // #region cleanupExpiredRefreshTokens

    @Test
    void shouldDeleteExpiredRefreshTokens() {
        when(refreshTokenRepository.deleteAllExpiredToken(any(Instant.class))).thenReturn(3);

        cleanupService.cleanupExpiredRefreshTokens();

        verify(refreshTokenRepository).deleteAllExpiredToken(any(Instant.class));
    }

    @Test
    void shouldHandleNoExpiredRefreshTokens() {
        when(refreshTokenRepository.deleteAllExpiredToken(any(Instant.class))).thenReturn(0);

        cleanupService.cleanupExpiredRefreshTokens();

        verify(refreshTokenRepository).deleteAllExpiredToken(any(Instant.class));
    }

    // #endregion
}