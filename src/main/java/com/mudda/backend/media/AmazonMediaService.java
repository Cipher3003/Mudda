package com.mudda.backend.media;

import com.mudda.backend.exceptions.*;
import com.mudda.backend.media.dto.BatchImageUploadResponse;
import com.mudda.backend.media.dto.ImageUploadResponse;
import com.mudda.backend.media.dto.MediaUploadRequest;
import com.mudda.backend.media.dto.MediaUploadResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Profile({"prod", "stage"})
public class AmazonMediaService implements MediaService {

//    TODO: detect images categories using s3

    private static final long MAX_SIZE_BYTES = 1024 * 1024;
    private static final int MAX_SIZE_MB = 1;

    private final S3Client s3Client;
    private final String bucketName;

    private final ImageValidator imageValidator;
    private final MediaRepository mediaRepository;
    private final MediaHelperService mediaHelperService;

    @Value("${app.cdn.origin}")
    private String cdnOrigin;

    public AmazonMediaService(
            @Value("${amazon.s3.bucket-name}") String bucketName,
            S3Client s3Client,
            ImageValidator imageValidator,
            MediaRepository mediaRepository,
            MediaHelperService mediaHelperService
    ) {
        this.bucketName = bucketName;
        this.s3Client = s3Client;
        this.imageValidator = imageValidator;
        this.mediaRepository = mediaRepository;
        this.mediaHelperService = mediaHelperService;
    }

    // region Queries (Read Operations)

    // NOTE: For testing only
    @Override
    public List<String> getImages() {
        List<String> objectKeys = new ArrayList<>();

        log.trace("Fetching bucket contents");
        try {
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(request);
            response.contents().forEach(s3Object ->
                    objectKeys.add("%s/%s".formatted(cdnOrigin, s3Object.key()))
            );

        } catch (Exception e) {
            log.error("Failed to list objects in S3 bucket: {}", bucketName, e);
        }

        return objectKeys;
    }

    // endregion

    // region Commands (Write Operations)

    @Override
    public ImageUploadResponse uploadImage(MultipartFile multipartFile) {

        imageValidator.validateImage(multipartFile);

        try {
            String publicId = mediaHelperService.generatePublicId();
            String filename = multipartFile.getOriginalFilename();
            String fileExtension = FilenameUtils.getExtension(filename);
            String mediaKey = mediaHelperService.getMediaKey(publicId, fileExtension);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(mediaKey)
                    .contentType(multipartFile.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(
                    multipartFile.getInputStream(), multipartFile.getSize()
            ));

            log.info("Uploaded image to AWS: {}", mediaKey);

            Media media = new Media(
                    publicId,
                    mediaKey,
                    UploadStatus.SUCCESS,
                    0
            );
            mediaHelperService.saveMedia(media);

            return new ImageUploadResponse(
                    filename,
                    mediaKey,
                    cdnOrigin.concat(mediaKey),
                    UploadStatus.SUCCESS,
                    null
            );

        } catch (IOException e) {
            throw new UploadFailedException();
        } catch (S3Exception e) {
            throw new S3ServiceException();
        } catch (SdkClientException e) {
            throw new S3ClientException();
        }
    }

    @Override
    public BatchImageUploadResponse uploadImages(List<MultipartFile> files) {
        log.trace("Starting batch upload for {} images", files != null ? files.size() : 0);
        if (files == null || files.isEmpty())
            return new BatchImageUploadResponse(0, 0, Collections.emptyList());

        List<ImageUploadResponse> responses = files.stream()
                .map(file -> {
                    try {
                        return this.uploadImage(file);
                    } catch (Exception e) {
                        log.error("Failed to upload image to AWS: {}", file.getOriginalFilename(), e);

                        String errorMessage = e.getMessage();
//                        TODO: give better error messages
                        if (errorMessage == null || errorMessage.isEmpty())
                            errorMessage = "Unknown error occurred (%s)".formatted(e.getClass().getSimpleName());

                        return new ImageUploadResponse(
                                file.getOriginalFilename(),
                                null,
                                null,
                                UploadStatus.FAILED,
                                errorMessage
                        );
                    }
                })
                .toList();

        int successCount = (int) responses.stream()
                .filter(response -> response.status().equals(UploadStatus.SUCCESS)).count();
        int failureCount = responses.size() - successCount;

        return new BatchImageUploadResponse(successCount, failureCount, responses);
    }

    @Override
    @Transactional
    public MediaUploadResponse initUpload(MediaUploadRequest request) {
        mediaHelperService.validateUploadRequest(request);

        String publicId = mediaHelperService.generatePublicId();
        String fileExtension = FilenameUtils.getExtension(request.fileName());
        String mediaKey = mediaHelperService.getMediaKey(publicId, fileExtension);

        mediaRepository.save(new Media(
                publicId, mediaKey, UploadStatus.UPLOADING, request.position()
        ));

        return new MediaUploadResponse(
                publicId,
                mediaHelperService.generatePresignedUrl(bucketName, mediaKey, request.contentType())
        );
        // TODO: add content-type header in frontend upload request
    }

    @Override
    @Transactional
    public List<MediaUploadResponse> initUploads(List<MediaUploadRequest> requests) {
        requests.forEach(mediaHelperService::validateUploadRequest);

        List<MediaUploadResponse> responses = new ArrayList<>(requests.size());
        List<Media> mediaList = new ArrayList<>(requests.size());

        for (MediaUploadRequest request : requests) {
            String publicId = mediaHelperService.generatePublicId();
            String fileExtension = FilenameUtils.getExtension(request.fileName());
            String mediaKey = mediaHelperService.getMediaKey(publicId, fileExtension);
            String presignedUrl = mediaHelperService.generatePresignedUrl(bucketName, mediaKey, request.contentType());

            mediaList.add(new Media(publicId, mediaKey, UploadStatus.UPLOADING, request.position()));
            responses.add(new MediaUploadResponse(publicId, presignedUrl));
        }

        mediaRepository.saveAll(mediaList);
        return responses;
    }

    @Override
    public void completeUpload(String publicId) {
        Media media = mediaRepository.findByPublicId(publicId).orElseThrow(EntityNotFoundException::new);

        try {
            HeadObjectResponse metadata = s3Client
                    .headObject(request -> request.bucket(bucketName).key(media.getMediaKey()));

            long size = metadata.contentLength();

            if (!metadata.contentType().startsWith("image/")) throw new NonImageFileException();
            if (size <= 0 || size > MAX_SIZE_BYTES) throw new FileSizeLimitExceededException(MAX_SIZE_MB);

            mediaHelperService.updateStatusSuccess(media, size);
        } catch (S3Exception | NonImageFileException | FileSizeLimitExceededException e) {
            mediaHelperService.updateStatusFailed(media);
            if (e instanceof S3Exception) throw new UploadFailedException();
            throw e;
        }
    }

    @Override
    @Transactional
    public int linkToIssue(long issueId, List<String> mediaKeys) {
        int rows = mediaRepository.updateOwner(MediaOwner.ISSUE, issueId, mediaKeys);
        log.debug("Updated owner to issue id {}, rows {}", issueId, rows);
        return rows;
    }

    @Override
    @Transactional
    public int linkToUser(long userId, String mediaKey) {
        int rows = mediaRepository.updateOwner(MediaOwner.USER, userId, List.of(mediaKey));
        log.debug("Updated owner to user id {}, rows {}", userId, rows);
        return rows;
    }

    @Override
    @Transactional
    public void deleteImage(String publicId) {
        int rows = mediaRepository.markFailed(publicId);
        if (rows != 1) throw new UploadFailedException();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void removeImageFromOwner(Long ownerId, MediaOwner mediaOwner) {
        int rows = mediaRepository.markFailedByOwnerIdAndOwnerType(ownerId, mediaOwner);
        log.debug("Marked {} Images from Owner: {}:{} for removal", rows, mediaOwner, ownerId);
    }

    // endregion

}
