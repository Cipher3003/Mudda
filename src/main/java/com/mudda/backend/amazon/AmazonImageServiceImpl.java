package com.mudda.backend.amazon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.mudda.backend.exceptions.S3ServiceException;
import com.mudda.backend.exceptions.EmptyFileException;
import com.mudda.backend.exceptions.FileConversionException;
import com.mudda.backend.exceptions.NonImageFileException;
import com.mudda.backend.exceptions.S3ClientException;
import com.mudda.backend.exceptions.FileSizeLimitExceededException;
import com.mudda.backend.exceptions.InvalidImageExtensionException;
import com.mudda.backend.utils.FileUtils;
import com.mudda.backend.utils.MessageCodes;

@Slf4j
@Service
public class AmazonImageServiceImpl implements AmazonImageService {

    private final String bucketName;
    private final AmazonS3 amazonS3;

    public AmazonImageServiceImpl(@Value("${amazon.s3.bucket-name}") String bucketName,
                                  AmazonS3 amazonS3) {
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
            ObjectListing objectListing = amazonS3.listObjects(bucketName);

            do {
                for (S3ObjectSummary summary : objectListing.getObjectSummaries()) {
                    objectKeys.add(summary.getKey());
                }
                objectListing = amazonS3.listNextBatchOfObjects(objectListing);
            } while (objectListing.isTruncated());

        } catch (Exception e) {
            log.error("Failed to list objects in S3 bucket: {}", bucketName, e);
        }

        return objectKeys;
    }

    // endregion

    // region Commands (Write Operations)

    @Transactional
    @Override
    public AmazonImage uploadImageToAmazon(MultipartFile file) {

        log.trace("Validating image file before uploading to AWS");
        // Check if file is empty or null
        if (file == null || file.isEmpty())
            throw new EmptyFileException();

        // Check if file size exceeds maximum size (1MB default)
        if (file.getSize() >= 1024 * 1024)
            throw new FileSizeLimitExceededException(1);

        List<String> validExtensions = List.of("jpeg", "png", "jpg");
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());

        // Check if file extensions are valid else throw InvalidImageExtensionException
        if (fileExtension == null ||
                validExtensions
                        .stream()
                        .noneMatch(ext -> ext.equalsIgnoreCase(fileExtension))
        )
            throw new InvalidImageExtensionException(String.join(", ", validExtensions));


        // Check if file is an actual image and MIME type is an image
        String fileContentType = file.getContentType();
        if (fileContentType == null || !fileContentType.startsWith("image/"))
            throw new NonImageFileException();

        try {
            if (ImageIO.read(file.getInputStream()) == null)
                throw new NonImageFileException();

        } catch (IOException e) {
            throw new NonImageFileException();
        }

        log.trace("Converting multipart file to image file for uploading to AWS");
        try {
            File imageFile = FileUtils.convertMultipartToFile(file);
            String fileName = FileUtils.generateFileName(file);

            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, imageFile));
            imageFile.delete();

            String fileUrl = getFileUrl(bucketName, amazonS3.getRegionName()).concat(fileName);
            log.info("Uploaded image to AWS: {}", fileUrl);

            return new AmazonImage(fileName, fileUrl);
        } catch (IOException e) {
            throw new FileConversionException();
        } catch (AmazonS3Exception e) {
            throw new S3ServiceException();
        } catch (SdkClientException e) {
            throw new S3ClientException();
        }
    }

    @Transactional
    @Override
    public void removeImageFromAmazon(String imageFileName) {

        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, imageFileName));
            log.info("Removed image from AWS: {}", imageFileName);
        } catch (AmazonS3Exception e) {
            throw new S3ServiceException();
        } catch (SdkClientException e) {
            throw new S3ClientException();
        }
    }

    // endregion

//    ------------------------------
//    Helpers
//    ------------------------------

    private String getFileUrl(String bucketName, String region) {
        return String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);
    }

}
