package com.mudda.backend.amazon.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mudda.backend.amazon.models.AmazonImage;
import com.mudda.backend.amazon.repositories.AmazonImageRepository;
import com.mudda.backend.exceptions.FileConversionException;
import com.mudda.backend.exceptions.InvalidImageExtensionException;
import com.mudda.backend.utils.FileUtils;
import com.mudda.backend.utils.MessageCodes;
import com.mudda.backend.utils.MessageUtil;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class AmazonS3ImageService {

    private AmazonS3 amazonS3;
    private AmazonImageRepository amazonImageRepository;

    @Value("${amazon.s3.bucket-name}")
    private String bucketName;

    @Value("${amazon.s3.region}")
    private String region;

    public AmazonS3ImageService(AmazonS3 amazonS3, AmazonImageRepository repository) {
        this.amazonS3 = amazonS3;
        amazonImageRepository = repository;
    }

    public String getUrl() {
        // Construct S3 public URL
        return String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);
    }

    public List<AmazonImage> insertImages(List<MultipartFile> images) {
        List<AmazonImage> amazonImages = new ArrayList<>();
        images.forEach(image -> amazonImages.add(uploadImageToAmazon(image)));
        return amazonImages;
    }

    public AmazonImage uploadImageToAmazon(MultipartFile image) {

        List<String> validExtensions = Arrays.asList("jpeg", "jpg", "png");

        String extension = FilenameUtils.getExtension(image.getOriginalFilename());
        if (!validExtensions.contains(extension)) {
            throw new InvalidImageExtensionException(validExtensions);
        } else {
            String url = uploadMultiPartFile(image);

            // Confirm the file is in S3
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            if (!checkImageUpload(fileName)) {
                log.error("File was not confirmed in S3: {}", fileName);
                throw new RuntimeException("Failed to confirm upload to S3.");
            } else {
                log.info("File with name: " + fileName + " Uploaded successfully to S3.");
            }

            AmazonImage amazonImage = new AmazonImage();
            amazonImage.setImageKey(fileName);
            amazonImage.setImageUrl(url);

            return amazonImageRepository.save(amazonImage);
        }
    }

    public void removeImageFromAmazon(String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        amazonImageRepository.deleteById(fileName);
    }

    private String uploadMultiPartFile(MultipartFile multipartFile) {
        String fileUrl;

        try {
            File file = FileUtils.convertMultipartToFile(multipartFile);

            String fileName = FileUtils.generateFileName(multipartFile);

            uploadPublicFile(fileName, file);

            file.delete();

            fileUrl = getUrl().concat(fileName);
        } catch (IOException e) {
            log.warn(MessageUtil.getMessage(MessageCodes.MULTIPART_TO_FILE_CONVERT_EXCEPT), e);
            throw new FileConversionException();
        }

        return fileUrl;
    }

    private void uploadPublicFile(String fileName, File file) {
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file));
    }

    public List<String> listBucketContents() {
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

    public boolean checkImageExists(String imageKey) {
        return amazonImageRepository.existsById(imageKey);
    }

    public boolean checkImageUpload(String imageKey) {
        try {
            return amazonS3.doesObjectExist(bucketName, imageKey);
        } catch (Exception e) {
            log.error("Error checking if file exists in S3: {}", imageKey, e);
            return false;
        }
    }
}
