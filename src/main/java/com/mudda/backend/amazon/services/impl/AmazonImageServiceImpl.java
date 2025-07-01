package com.mudda.backend.amazon.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.mudda.backend.amazon.models.AmazonImage;
import com.mudda.backend.amazon.repositories.AmazonImageRepository;
import com.mudda.backend.amazon.services.AmazonImageService;
import com.mudda.backend.exceptions.DatabaseSaveException;
import com.mudda.backend.exceptions.EmptyFileException;
import com.mudda.backend.exceptions.FileConversionException;
import com.mudda.backend.exceptions.FileSizeLimitExceedException;
import com.mudda.backend.exceptions.InvalidImageExtensionException;
import com.mudda.backend.utils.FileUtils;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class AmazonImageServiceImpl implements AmazonImageService {

    @Value("${amazon.s3.bucket-name}")
    private String bucketName;

    private AmazonS3 amazonS3;
    private AmazonImageRepository amazonImageRepository;

    public AmazonImageServiceImpl(AmazonS3 amazonS3, AmazonImageRepository amazonImageRepository) {
        this.amazonS3 = amazonS3;
        this.amazonImageRepository = amazonImageRepository;
    }

    @Override
    public AmazonImage uploadImageToAmazon(MultipartFile file) {

        // Check if file is empty or null
        if (file.isEmpty()) {
            throw new EmptyFileException();
        }

        final double MB = 1e6;
        // Check if file size exceeds maximum size (1MB default)
        if (file.getSize() >= MB) {
            throw new FileSizeLimitExceedException(file.getSize(), ((long) MB));
        }

        List<String> validExtensions = List.of("jpeg", "png", "jpg");
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());

        // Check if file extensions are valid else throw InvalidImageExntesionException
        if (!validExtensions.contains(fileExtension)) {
            throw new InvalidImageExtensionException(validExtensions);
        }

        try {
            File imageFile = FileUtils.convertMultipartToFile(file);

            String fileName = FileUtils.generateFileName(file);

            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, imageFile));

            imageFile.delete();

            String fileUrl = getFileUrl(bucketName, amazonS3.getRegionName()).concat(fileName);

            AmazonImage amazonImage = new AmazonImage(fileName, fileUrl);

            amazonImageRepository.save(amazonImage);

            return amazonImage;
        } catch (IOException e) {
            throw new FileConversionException();
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            throw new DatabaseSaveException(e);
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
