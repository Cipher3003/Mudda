package com.mudda.backend.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mudda.backend.exceptions.FileConversionException;
import com.mudda.backend.exceptions.InvalidImageExtensionException;
import com.mudda.backend.models.AmazonImage;
import com.mudda.backend.repositories.AmazonImageRepository;
import com.mudda.backend.utils.FileUtils;
import com.mudda.backend.utils.MessageCodes;
import com.mudda.backend.utils.MessageUtil;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class AmazonS3ImageService extends AmazonClientService {

    private AmazonImageRepository amazonImageRepository;

    public List<AmazonImage> insertImages(List<MultipartFile> images) {
        List<AmazonImage> amazonImages = new ArrayList<>();
        images.forEach(image -> amazonImages.add(uploadImageToAmazon(image)));
        return amazonImages;
    }

    public AmazonImage uploadImageToAmazon(MultipartFile image) {

        List<String> validExtensions = Arrays.asList(".jpeg", "jpg", "png");

        String extension = FilenameUtils.getExtension(image.getOriginalFilename());
        if (!validExtensions.contains(extension)) {
            throw new InvalidImageExtensionException(validExtensions);
        } else {
            String url = uploadMultiPartFile(image);

            AmazonImage amazonImage = new AmazonImage();
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
        getAmazonS3().putObject(new PutObjectRequest(getBucketName(), fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }
}
