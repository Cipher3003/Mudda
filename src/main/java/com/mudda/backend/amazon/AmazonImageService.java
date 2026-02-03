package com.mudda.backend.amazon;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface AmazonImageService {

    List<String> getBucketContents();

    ImageUploadResponse uploadImageToAmazon(MultipartFile file);

    BatchImageUploadResponse uploadImagesToAmazon(List<MultipartFile> files);

    void removeImageFromAmazon(String imageFileName);

}
