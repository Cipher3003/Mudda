package com.mudda.backend.amazon.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.mudda.backend.amazon.models.AmazonImage;
import com.mudda.backend.amazon.repositories.AmazonImageRepository;
import com.mudda.backend.utils.FileUtils;

@ExtendWith(MockitoExtension.class)
public class AmazonImageServiceImplTest {

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private AmazonImageRepository amazonImageRepository;

    @InjectMocks
    private AmazonImageServiceImpl amazonImageServiceImpl;

    @Test
    void testUploadImageToAmazon() {

        String bucketName = "media-url-devbucket-2026";
        String bucketRegion = "eu-north-1";
        String testImageName = "testImage.png";
        String testImageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, bucketRegion,
                testImageName);
        AmazonImage testAmazonImage = new AmazonImage(testImageName, testImageUrl);

        // AmazonImageServiceImpl setup
        amazonImageServiceImpl = new AmazonImageServiceImpl(bucketName, amazonS3, amazonImageRepository);

        // AmazonS3 setup
        when(amazonS3.getRegionName()).thenReturn(bucketRegion);

        try (MockedStatic<FileUtils> mockedStatic = mockStatic(FileUtils.class)) {

            mockedStatic.when(() -> FileUtils.generateFileName(any())).thenReturn(testImageName);
            mockedStatic.when(() -> FileUtils.convertMultipartToFile(any())).thenReturn(new File(testImageName));

            // AmazonImageRepository setup
            when(amazonImageRepository.save(
                    argThat(image -> testImageName.equals(image.getImageName()) &&
                            testImageUrl.equals(image.getImageUrl()))))
                    .thenReturn(testAmazonImage);

            MockMultipartFile mockMultipartFile = new MockMultipartFile(
                    "file",
                    testImageName,
                    "image/png",
                    testImageName.getBytes());

            AmazonImage actualAmazonImage = amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile);
            assertEquals(testImageName, actualAmazonImage.getImageName());
            assertEquals(testImageUrl, actualAmazonImage.getImageUrl());
        }
    }
}
