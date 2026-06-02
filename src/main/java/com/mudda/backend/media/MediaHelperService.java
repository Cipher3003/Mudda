/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : MediaStatusService
 * Author  : Vikas Kumar
 * Created : 17-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.media;

import com.mudda.backend.exceptions.InvalidImageExtensionException;
import com.mudda.backend.exceptions.NonImageFileException;
import com.mudda.backend.media.dto.MediaUploadRequest;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class MediaHelperService {

    public static final List<String> VALID_CONTENT_TYPES = List.of("image/jpeg", "image/jpg", "image/png");
    private static final List<String> VALID_EXTENSIONS = List.of("jpeg", "jpg", "png");
    public static final int PRESIGNED_DURATION_MIN = 15;

    private final MediaRepository mediaRepository;
    private final S3Presigner s3Presigner;

    public MediaHelperService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
        s3Presigner = S3Presigner.create();
    }

    @Transactional
    public void saveMedia(Media media) {
        mediaRepository.save(media);
    }

    @Transactional
    public void updateStatusSuccess(Media media, long size) {
        media.setStatus(UploadStatus.SUCCESS);
        media.setSize(size);
        mediaRepository.save(media);
    }

    @Transactional
    public void updateStatusFailed(Media media) {
        media.setStatus(UploadStatus.FAILED);
        mediaRepository.save(media);
    }


    public void validateUploadRequest(MediaUploadRequest request) {
        if (VALID_CONTENT_TYPES.stream().noneMatch(request.contentType()::equalsIgnoreCase))
            throw new NonImageFileException();

        String extension = FilenameUtils.getExtension(request.fileName());

        if (VALID_EXTENSIONS.stream().noneMatch(extension::equalsIgnoreCase))
            throw new InvalidImageExtensionException(String.join(", ", VALID_EXTENSIONS));
    }

    public String generatePublicId() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer.array());
    }

    public String getMediaKey(String publicId, String extension) {
        // NOTE: preferred but not implemented format - {prefix}/{ownerType}/{publicId}/{variant}.{ext}
        // TODO: add owner type in key
        return String.format("media/%s/original.%s", publicId, extension).replace(" ", "_");
    }

    public String generatePresignedUrl(String bucketName, String key, String contentType) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(PRESIGNED_DURATION_MIN))
                .putObjectRequest(objectRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }
}
