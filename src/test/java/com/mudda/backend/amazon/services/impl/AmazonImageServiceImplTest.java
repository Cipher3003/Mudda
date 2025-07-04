package com.mudda.backend.amazon.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.mudda.backend.amazon.ContentType;
import com.mudda.backend.amazon.models.AmazonImage;
import com.mudda.backend.exceptions.EmptyFileException;
import com.mudda.backend.exceptions.FileConversionException;
import com.mudda.backend.exceptions.FileNotImageException;
import com.mudda.backend.exceptions.FileSizeLimitExceedException;
import com.mudda.backend.exceptions.InvalidImageExtensionException;
import com.mudda.backend.utils.FileUtils;

@ExtendWith(MockitoExtension.class)
public class AmazonImageServiceImplTest {

        @Mock
        private AmazonS3 amazonS3;

        private AmazonImageServiceImpl amazonImageServiceImpl;

        final String bucketName = "media-url-devbucket-2026";
        final String bucketRegion = "eu-north-1";
        final String testImageName = "testImage.jpg";

        private MockMultipartFile createMockFile(String originalFileName, ContentType fileType, byte[] content) {
                return new MockMultipartFile("file", originalFileName, fileType.getValue(), content);
        }

        private MockMultipartFile createMockFileFromResource(
                        String originalFileName, ContentType fileType) throws IOException {
                return new MockMultipartFile("file", originalFileName, fileType.getValue(),
                                new ClassPathResource(originalFileName).getInputStream());
        }

        @BeforeEach
        void setUp() {
                // AmazonImageServiceImpl setup
                amazonImageServiceImpl = new AmazonImageServiceImpl(bucketName, amazonS3);
        }

        // #region Success Case
        @Test
        void shouldUploadImageSuccessfully() throws IOException {

                String testImageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                                bucketName, bucketRegion, testImageName);

                // AmazonS3 setup
                when(amazonS3.getRegionName()).thenReturn(bucketRegion);

                try (MockedStatic<FileUtils> mockedStatic = mockStatic(FileUtils.class)) {

                        mockedStatic.when(() -> FileUtils.generateFileName(any()))
                                        .thenReturn(testImageName);
                        mockedStatic.when(() -> FileUtils.convertMultipartToFile(any()))
                                        .thenReturn(new File(testImageName));

                        MockMultipartFile mockMultipartFile = createMockFileFromResource(
                                        testImageName, ContentType.IMAGE_JPG);

                        AmazonImage actualAmazonImage = amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile);

                        assertEquals(testImageName, actualAmazonImage.getImageName());
                        assertEquals(testImageUrl, actualAmazonImage.getImageUrl());
                }
        }

        // #endregion

        // #region Input Validation Tests
        @Test
        void shouldThrowWhenFileIsEmpty() {

                MockMultipartFile mockMultipartFile = createMockFile(
                                testImageName, ContentType.IMAGE_JPG, "".getBytes());

                assertThrows(EmptyFileException.class, () -> {
                        amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile);
                });
        }

        @Test
        void shouldThrowWhenFileIsNull() {

                assertThrows(EmptyFileException.class, () -> {
                        amazonImageServiceImpl.uploadImageToAmazon(null);
                });
        }

        @Test
        void shoudlThrowWhenFileIsTooLarge() {

                int MB = 1024 * 1024;
                byte[] content = new byte[MB];
                Arrays.fill(content, (byte) 0xff);

                MockMultipartFile mockMultipartFile = createMockFile(testImageName, ContentType.IMAGE_JPG, content);

                assertThrows(FileSizeLimitExceedException.class, () -> {
                        amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile);
                });
        }

        // #endregion

        // #region File type & content validation tests
        @Test
        void shouldThrowWhenContentTypeIsNotImage() {

                MockMultipartFile mockMultipartFile = createMockFile(
                                testImageName, ContentType.TEXT_PLAIN, testImageName.getBytes());

                assertThrows(FileNotImageException.class, () -> {
                        amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile);
                });
        }

        @Test
        void shouldThrowWhenFileContentIsNotActualImage() {

                MockMultipartFile mockMultipartFile = createMockFile(
                                testImageName, ContentType.IMAGE_JPG, testImageName.getBytes());

                assertThrows(FileNotImageException.class, () -> {
                        amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile);
                });
        }

        @Test
        void shouldThrowWhenImageHasInvalidExtension() throws IOException {

                String testImageName = "testImageInvalidExt.webp";
                MockMultipartFile mockMultipartFile = createMockFileFromResource(testImageName, ContentType.IMAGE_WEBP);

                assertThrows(InvalidImageExtensionException.class, () -> {
                        amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile);
                });
        }

        // #endregion

        // #region Simulated internal failure scenarios
        @Test
        void shouldThrowWhenFileConversionFails() throws IOException {

                MockMultipartFile mockMultipartFile = createMockFileFromResource(testImageName, ContentType.IMAGE_JPG);

                try (MockedStatic<FileUtils> mockFileUtils = Mockito.mockStatic(FileUtils.class)) {

                        mockFileUtils.when(() -> FileUtils.convertMultipartToFile(mockMultipartFile))
                                        .thenThrow(IOException.class);

                        assertThrows(FileConversionException.class, () -> {
                                amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile);
                        });

                }
        }

        // #endregion

}
