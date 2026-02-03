package com.mudda.backend.amazon;

import com.mudda.backend.exceptions.*;
import com.mudda.backend.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mudda.backend.utils.FileUtils.getPublicUrl;

@Slf4j
@Service
public class AmazonImageServiceImpl implements AmazonImageService {

//    TODO: detect images categories using s3
//    TODO: upload image using presigned URL

    private final String bucketName;
    private final S3Client amazonS3;


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

    @Transactional
    @Override
    public AmazonImage uploadImageToAmazon(MultipartFile multipartFile) {

        log.trace("Validating image file before uploading to AWS");
        // Check if file is empty or null
        if (multipartFile == null || multipartFile.isEmpty())
            throw new EmptyFileException();

        // Check if file size exceeds maximum size (1MB default)
        if (multipartFile.getSize() >= 1024 * 1024)
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

        try {
            // TODO: this fails for webp fix it maybe
            if (ImageIO.read(multipartFile.getInputStream()) == null)
                throw new NonImageFileException();

        } catch (IOException e) {
            throw new NonImageFileException();
        }

        log.trace("Converting multipart file to image file for uploading to AWS");
        try {
            String fileName = FileUtils.generateFileName(multipartFile);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(fileContentType)
                    .build();

//            TODO: only good for small file (10-20MB) consider fromInputStream
            amazonS3.putObject(putObjectRequest, RequestBody.fromBytes(multipartFile.getBytes()));

            String fileUrl = getPublicUrl(fileName);
            log.info("Uploaded image to AWS: {}", fileUrl);

            return new AmazonImage(fileName, fileUrl);
        } catch (IOException e) {
            throw new FileConversionException();
        } catch (S3Exception e) {
            throw new S3ServiceException();
        } catch (SdkClientException e) {
            throw new S3ClientException();
        }
    }

    @Transactional
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
        } catch (SdkClientException e) {
            throw new S3ClientException();
        }
    }

//    TODO: add delete multiple images
//    Image upload flow -> client send image to upload API get public url send them to issue API (maybe only send keys)
//    then show issue when asked with public url

    // endregion

}
