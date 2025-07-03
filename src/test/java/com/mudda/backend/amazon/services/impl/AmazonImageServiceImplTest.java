package com.mudda.backend.amazon.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.mudda.backend.amazon.models.AmazonImage;
import com.mudda.backend.amazon.repositories.AmazonImageRepository;
import com.mudda.backend.exceptions.EmptyFileException;
import com.mudda.backend.exceptions.FileNotImageException;
import com.mudda.backend.exceptions.FileSizeLimitExceedException;
import com.mudda.backend.exceptions.InvalidImageExtensionException;
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
        void testUploadImageToAmazon() throws IOException {

                String bucketName = "media-url-devbucket-2026";
                String bucketRegion = "eu-north-1";
                String testImageName = "testImage.jpg";
                String testImageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, bucketRegion,
                                testImageName);

                AmazonImage testAmazonImage = new AmazonImage(testImageName, testImageUrl);
                ClassPathResource resource = new ClassPathResource(testImageName);

                // AmazonImageServiceImpl setup
                amazonImageServiceImpl = new AmazonImageServiceImpl(bucketName, amazonS3, amazonImageRepository);

                // AmazonS3 setup
                when(amazonS3.getRegionName()).thenReturn(bucketRegion);

                try (MockedStatic<FileUtils> mockedStatic = mockStatic(FileUtils.class)) {

                        mockedStatic.when(() -> FileUtils.generateFileName(any())).thenReturn(testImageName);
                        mockedStatic.when(() -> FileUtils.convertMultipartToFile(any()))
                                        .thenReturn(new File(testImageName));

                        // AmazonImageRepository setup
                        when(amazonImageRepository.save(
                                        argThat(image -> testImageName.equals(image.getImageName()) &&
                                                        testImageUrl.equals(image.getImageUrl()))))
                                        .thenReturn(testAmazonImage);

                        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                                        "file",
                                        testImageName,
                                        "image/png",
                                        resource.getInputStream());

                        AmazonImage actualAmazonImage = amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile);
                        assertEquals(testImageName, actualAmazonImage.getImageName());
                        assertEquals(testImageUrl, actualAmazonImage.getImageUrl());
                }
        }

        @Test
        void testUploadImageToAmazonEmptyFile() {

                String testImageName = "testImage.png";

                MockMultipartFile mockMultipartFile = new MockMultipartFile(
                                "file",
                                testImageName,
                                "image/png",
                                "".getBytes());

                assertThrows(EmptyFileException.class, () -> {
                        amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile);
                });
        }

        @Test
        void testUploadImageToAmazonNullFile() {

                assertThrows(EmptyFileException.class, () -> {
                        amazonImageServiceImpl.uploadImageToAmazon(null);
                });
        }

        @Test
        void testUploadImageToAmazonTooBigFile() {

                int MB = 1024 * 1024;
                byte[] content = new byte[MB];
                String testImageName = "testImage.png";

                Arrays.fill(content, (byte) 0xff);

                MockMultipartFile mockMultipartFile = new MockMultipartFile(
                                "file",
                                testImageName,
                                "image/png",
                                content);

                assertThrows(FileSizeLimitExceedException.class, () -> {
                        amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile);
                });
        }

        @Test
        void testUploadImageToAmazonNonImageContentType() {

                String testImageName = "testImage.png";
                MockMultipartFile mockMultipartFile = new MockMultipartFile(
                                "file",
                                testImageName,
                                "text/plain",
                                testImageName.getBytes());

                assertThrows(FileNotImageException.class, () -> {
                        amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile);
                });
        }

        @Test
        void testUploadImageToAmazonNonImageContent() {

                String testImageName = "testImage.png";
                MockMultipartFile mockMultipartFile = new MockMultipartFile(
                                "file",
                                testImageName,
                                "image/png",
                                testImageName.getBytes());

                assertThrows(FileNotImageException.class, () -> {
                        amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile);
                });
        }

        @Test
        void testUploadImageToAmazonInvalidImageType() throws IOException {

                String testImageName = "testImageInvalidExt.webp";
                ClassPathResource resource = new ClassPathResource(testImageName);

                MockMultipartFile mockMultipartFile = new MockMultipartFile(
                                "file",
                                testImageName,
                                "image/webp",
                                resource.getInputStream());

                assertThrows(InvalidImageExtensionException.class, () -> {
                        amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile);
                });
        }

        

}
