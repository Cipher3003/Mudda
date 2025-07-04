package com.mudda.backend.amazon.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.mudda.backend.amazon.models.AmazonImage;
import com.mudda.backend.amazon.services.AmazonImageService;
import com.mudda.backend.exceptions.S3ServiceException;
import com.mudda.backend.exceptions.EmptyFileException;
import com.mudda.backend.exceptions.FileConversionException;
import com.mudda.backend.exceptions.NonImageFileException;
import com.mudda.backend.exceptions.S3ClientException;
import com.mudda.backend.exceptions.FileSizeLimitExceededException;
import com.mudda.backend.exceptions.InvalidImageExtensionException;
import com.mudda.backend.utils.FileUtils;
import com.mudda.backend.utils.MessageCodes;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class AmazonImageServiceImpl implements AmazonImageService {

    private String bucketName;
    private AmazonS3 amazonS3;

    public AmazonImageServiceImpl(@Value("${amazon.s3.bucket-name}") String bucketName,
            AmazonS3 amazonS3) {
        this.bucketName = bucketName;
        this.amazonS3 = amazonS3;
    }

    @Override
    public AmazonImage uploadImageToAmazon(MultipartFile file) {

        // Check if file is empty or null
        if (file == null || file.isEmpty()) {
            throw new EmptyFileException(MessageCodes.EMPTY_FILE);
        }

        // Check if file size exceeds maximum size (1MB default)
        if (file.getSize() >= 1024 * 1024) {
            throw new FileSizeLimitExceededException(
                    MessageCodes.FILE_SIZE_EXCEED_LIMIT,
                    file.getSize(),
                    1024 * 1024);
        }

        List<String> validExtensions = List.of("jpeg", "png", "jpg");
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());

        // Check if file extensions are valid else throw InvalidImageExntesionException
        if (fileExtension == null ||
                validExtensions
                        .stream()
                        .noneMatch(ext -> ext.equalsIgnoreCase(fileExtension))) {
            throw new InvalidImageExtensionException(
                    MessageCodes.INVALID_IMAGE_EXTENSION,
                    String.join(", ", validExtensions));
        }

        // Check if file is an actual image and MIME type is an image
        String fileContentType = file.getContentType();
        if (fileContentType == null || !fileContentType.startsWith("image/")) {
            throw new NonImageFileException(MessageCodes.FILE_NOT_IMAGE);
        }

        try {
            if (ImageIO.read(file.getInputStream()) == null) {
                throw new NonImageFileException(MessageCodes.FILE_NOT_IMAGE);
            }
        } catch (IOException e) {
            throw new NonImageFileException(MessageCodes.FILE_NOT_IMAGE);
        }

        try {
            File imageFile = FileUtils.convertMultipartToFile(file);
            String fileName = FileUtils.generateFileName(file);

            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, imageFile));
            imageFile.delete();

            String fileUrl = getFileUrl(bucketName, amazonS3.getRegionName()).concat(fileName);

            AmazonImage amazonImage = new AmazonImage(fileName, fileUrl);
            return amazonImage;
        } catch (IOException e) {
            throw new FileConversionException(MessageCodes.MULTIPART_TO_FILE_CONVERT_EXCEPT);
        } catch (AmazonS3Exception e) {
            throw new S3ServiceException(MessageCodes.AMAZON_ERROR);
        } catch (SdkClientException e) {
            throw new S3ClientException(MessageCodes.AMAZON_CLIENT_ERROR);
        }
    }

    private String getFileUrl(String bucketName, String region) {
        return String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);
    }

    // NOTE: For testing only
    @Override
    public List<String> getBucketContents() {
        List<String> objectKeys = new ArrayList<>();
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

}
