package com.mudda.backend.amazon;

import com.mudda.backend.exceptions.S3ClientException;
import com.mudda.backend.exceptions.S3ServiceException;
import com.mudda.backend.exceptions.UploadFailedException;
import com.mudda.backend.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mudda.backend.utils.FileUtils.getPublicUrl;

@Slf4j
@Service
public class AmazonImageServiceImpl implements AmazonImageService {

//    TODO: detect images categories using s3
//    TODO: upload image using presigned URL -> maybe not worth it

    private final S3Client amazonS3;
    private final ImageValidator imageValidator;
    private final String bucketName;


    public AmazonImageServiceImpl(@Value("${amazon.s3.bucket-name}") String bucketName,
                                  S3Client amazonS3, ImageValidator imageValidator) {
        this.bucketName = bucketName;
        this.amazonS3 = amazonS3;
        this.imageValidator = imageValidator;
    }

    // region Queries (Read Operations)

    // NOTE: For testing only
    @Override
    public List<String> getBucketContents() {
        List<String> objectKeys = new ArrayList<>();

        log.trace("Fetching bucket contents");
        try {
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsV2Response response = amazonS3.listObjectsV2(request);
            response.contents().forEach(s3Object -> objectKeys.add(getPublicUrl(s3Object.key())));
        } catch (Exception e) {
            log.error("Failed to list objects in S3 bucket: {}", bucketName, e);
        }

        return objectKeys;
    }

    // endregion

    // region Commands (Write Operations)

    //    TODO: maybe cache file to database to resume uploads to handle fallback ?
    @Override
    public ImageUploadResponse uploadImageToAmazon(MultipartFile multipartFile) {

        imageValidator.validateImage(multipartFile);

        try {
            String fileKey = FileUtils.generateFileName(multipartFile);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .contentType(multipartFile.getContentType())
                    .build();

            amazonS3.putObject(putObjectRequest, RequestBody.fromInputStream(
                    multipartFile.getInputStream(), multipartFile.getSize()
            ));

            log.info("Uploaded image to AWS: {}", fileKey);

            return new ImageUploadResponse(
                    multipartFile.getOriginalFilename(),
                    fileKey,
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
    public BatchImageUploadResponse uploadImagesToAmazon(List<MultipartFile> files) {
        log.trace("Starting batch upload for {} images", files != null ? files.size() : 0);
        if (files == null || files.isEmpty())
            return new BatchImageUploadResponse(0, 0, Collections.emptyList());

//        Process all files in parallel
        List<ImageUploadResponse> responses = files.parallelStream()
                .map(file -> {
                    try {
                        return this.uploadImageToAmazon(file);
                    } catch (Exception e) {
                        log.error("Failed to upload image to AWS: {}", file.getOriginalFilename(), e);

                        String errorMessage = e.getMessage();
//                        TODO: give better error messages
                        if (errorMessage == null || errorMessage.isEmpty())
                            errorMessage = "Unknown error occurred (%s)".formatted(e.getClass().getSimpleName());

                        return new ImageUploadResponse(
                                file.getOriginalFilename(),
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
    public void removeImageFromAmazon(String imageFileName) {

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(imageFileName)
                    .build();

            amazonS3.deleteObject(deleteObjectRequest);
            log.info("Removed image from AWS: {}", imageFileName);
        } catch (S3Exception e) {
            throw new S3ServiceException();
        }
//        catch (AwsServiceException e) {
//        TODO: handle no such entity in aws s3
//        }
        catch (SdkClientException e) {
            throw new S3ClientException();
        }
    }

//    TODO: add delete multiple images

    // endregion

}
