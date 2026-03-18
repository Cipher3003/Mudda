/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : MediaCleanupService
 * Author  : Vikas Kumar
 * Created : 17-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.media;

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
public class MediaCleanupService {

    private final MediaRepository mediaRepository;
    private final S3Client s3Client;

    @Value("${amazon.s3.bucket-name}")
    private String bucketName;

    public MediaCleanupService(MediaRepository mediaRepository, S3Client s3Client) {
        this.mediaRepository = mediaRepository;
        this.s3Client = s3Client;
    }

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
}
