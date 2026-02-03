package com.mudda.backend.amazon;

import com.mudda.backend.exceptions.*;
import com.mudda.backend.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mudda.backend.utils.FileUtils.getPublicUrl;

@Slf4j
@Service
public class AmazonImageServiceImpl implements AmazonImageService {

//    TODO: detect images categories using s3
//    TODO: upload image using presigned URL -> maybe not worth it

    private final String bucketName;
    private final S3Client amazonS3;

    private final static Tika tika = new Tika();

    public AmazonImageServiceImpl(@Value("${amazon.s3.bucket-name}") String bucketName, S3Client amazonS3) {
        this.bucketName = bucketName;
        this.amazonS3 = amazonS3;
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
    public AmazonImage uploadImageToAmazon(MultipartFile multipartFile) {

        log.trace("Validating image file before uploading to AWS");
        // Check if file is empty or null
        if (multipartFile == null || multipartFile.isEmpty())
            throw new EmptyFileException();

        // Check if file size exceeds maximum size (1MB default)
        if (multipartFile.getSize() >= 1024 * 1024) // TODO: remove hardcoded constraints
            throw new FileSizeLimitExceededException(1);

        List<String> validExtensions = List.of("jpeg", "png", "jpg");
        String fileExtension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());

        // Check if file extensions are valid else throw InvalidImageExtensionException
        if (fileExtension == null ||
                validExtensions
                        .stream()
                        .noneMatch(ext -> ext.equalsIgnoreCase(fileExtension)))
            throw new InvalidImageExtensionException(String.join(", ", validExtensions));

        // Check if file is an actual image and MIME type is an image
        String fileContentType = multipartFile.getContentType();
        if (fileContentType == null || !fileContentType.startsWith("image/"))
            throw new NonImageFileException();

        // TODO: this fails for webp fix it maybe
        try {
            String detectedType = tika.detect(multipartFile.getInputStream());
            if (!detectedType.startsWith("image/")) throw new NonImageFileException();

        } catch (IOException e) {
            throw new NonImageFileException();
        }

        try {
            String fileKey = FileUtils.generateFileName(multipartFile);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .contentType(fileContentType)
                    .build();

//            TODO: only good for small file (10-20MB) consider fromInputStream
            amazonS3.putObject(putObjectRequest, RequestBody.fromInputStream(
                    multipartFile.getInputStream(), multipartFile.getSize()
            ));

            log.info("Uploaded image to AWS: {}", fileKey);

            return new AmazonImage(multipartFile.getOriginalFilename(), fileKey);
        } catch (IOException e) {
            throw new UploadFailedException();
        } catch (S3Exception e) {
            throw new S3ServiceException();
        } catch (SdkClientException e) {
            throw new S3ClientException();
        }
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
