/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : MockImageServiceImpl
 * Author  : Vikas Kumar
 * Created : 11-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.amazon;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
public class MockImageServiceImpl implements AmazonImageService {

    @PostConstruct
    public void init() {
        log.info("MockImageServiceImpl bean initialized. Mock service is being used.");
    }

    ImageUploadResponse mockResponse = new ImageUploadResponse(
            "originalFile",
            "FileKey",
            "http://someplaceholder.com",
            UploadStatus.SUCCESS,
            null
    );

    @Override
    public List<String> getBucketContents() {
        return List.of();
    }

    @Override
    public ImageUploadResponse uploadImageToAmazon(MultipartFile file) {
        return mockResponse;
    }

    @Override
    public BatchImageUploadResponse uploadImagesToAmazon(List<MultipartFile> files) {
        return new BatchImageUploadResponse(files.size(), 0, List.of(mockResponse));
    }

    @Override
    public void removeImageFromAmazon(String imageFileName) {
    }
}
