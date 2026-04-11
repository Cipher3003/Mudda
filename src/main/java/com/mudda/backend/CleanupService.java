/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : MediaCleanupService
 * Author  : Vikas Kumar
 * Created : 17-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend;

import com.mudda.backend.media.Media;
import com.mudda.backend.media.MediaRepository;
import com.mudda.backend.token.refresh.RefreshTokenRepository;
import com.mudda.backend.token.verification.VerificationTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

@Service
@Slf4j
public class CleanupService {

    private final S3Client s3Client;
    private final MediaRepository mediaRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${amazon.s3.bucket-name}")
    private String bucketName;

    public CleanupService(
            MediaRepository mediaRepository,
            S3Client s3Client,
            VerificationTokenRepository verificationTokenRepository,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.mediaRepository = mediaRepository;
        this.s3Client = s3Client;
        this.verificationTokenRepository = verificationTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // TODO: transactional open for too long, consider page delete or batch delete after all s3 deletion
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupFailedUploads() {
        log.info("Starting daily cleanup of failed media uploads ...");

        Instant threshold = Instant.now().minus(24, ChronoUnit.HOURS);

        try (Stream<Media> mediaStream = mediaRepository.findExpiredOrFailed(threshold)) {
            mediaStream.forEach(media -> {
                try {
                    s3Client.deleteObject(request -> request.bucket(bucketName).key(media.getMediaKey()));

                    mediaRepository.delete(media);

                    log.info("Successfully deleted failed media: {}", media.getPublicId());
                } catch (Exception e) {
                    log.error("Failed to delete failed media: {}: {}", media.getPublicId(), e.getMessage());
                }
            });
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupExpiredVerificationTokens() {
        log.info("Starting daily cleanup of verification tokens ...");
        int deleted = verificationTokenRepository.deleteAllExpiredToken(Instant.now());
        log.info("Deleted {} expired verification tokens", deleted);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * * ")
    public void cleanupExpiredRefreshTokens() {
        log.info("Starting daily cleanup of refresh tokens ...");
        int deleted = refreshTokenRepository.deleteAllExpiredToken(Instant.now());
        log.info("Deleted {} expired refresh tokens", deleted);
    }
}
