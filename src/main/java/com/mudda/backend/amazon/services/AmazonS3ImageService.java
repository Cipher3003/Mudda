package com.mudda.backend.amazon.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mudda.backend.amazon.models.AmazonImage;
import com.mudda.backend.amazon.repositories.AmazonImageRepository;
import com.mudda.backend.common.config.AmazonConfig;
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
public class AmazonS3ImageService extends AmazonConfig {

    private AmazonImageRepository amazonImageRepository;

    public AmazonS3ImageService(AmazonImageRepository repository) {
        amazonImageRepository = repository;
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

    public void removeImageFromAmazon(AmazonImage amazonImage) {
        String fileName = amazonImage
                .getImageUrl()
                .substring(amazonImage.getImageUrl().lastIndexOf("/") + 1);
        getAmazonS3().deleteObject(new DeleteObjectRequest(getBucketName(), fileName));
        amazonImageRepository.delete(amazonImage);
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
        getAmazonS3().putObject(new PutObjectRequest(getBucketName(), fileName, file));
    }

    public List<String> listBucketContents() {
        List<String> objectKeys = new ArrayList<>();
        try {
            ObjectListing objectListing = getAmazonS3().listObjects(getBucketName());

            do {
                for (S3ObjectSummary summary : objectListing.getObjectSummaries()) {
                    objectKeys.add(summary.getKey());
                }
                objectListing = getAmazonS3().listNextBatchOfObjects(objectListing);
            } while (objectListing.isTruncated());

        } catch (Exception e) {
            log.error("Failed to list objects in S3 bucket: {}", getBucketName(), e);
        }

        return objectKeys;
    }

    public boolean checkImageExists(String imageKey) {
        return amazonImageRepository.existsById(imageKey);
    }

    public boolean checkImageUpload(String imageKey) {
        try {
            return getAmazonS3().doesObjectExist(getBucketName(), imageKey);
        } catch (Exception e) {
            log.error("Error checking if file exists in S3: {}", imageKey, e);
            return false;
        }
    }
}
