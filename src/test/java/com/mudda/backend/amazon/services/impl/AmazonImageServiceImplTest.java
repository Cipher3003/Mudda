package com.mudda.backend.amazon.services.impl;

import com.mudda.backend.amazon.AmazonImage;
import com.mudda.backend.amazon.AmazonImageServiceImpl;
import com.mudda.backend.amazon.ContentType;
import com.mudda.backend.exceptions.*;
import com.mudda.backend.utils.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AmazonImageServiceImplTest {

    @Mock
    private S3Client amazonS3;

    private AmazonImageServiceImpl amazonImageServiceImpl;

    final String bucketName = "media-url-devbucket-2026";
    final String testImageName = "testImage.jpg";

    private MockMultipartFile createMockFile(ContentType fileType, byte[] content) {
        return new MockMultipartFile("file", "testImage.jpg", fileType.getValue(), content);
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

        try (MockedStatic<FileUtils> mockedStatic = mockStatic(FileUtils.class)) {

            mockedStatic.when(() -> FileUtils.generateFileName(any()))
                    .thenReturn(testImageName);

            MockMultipartFile mockMultipartFile = createMockFileFromResource(
                    testImageName, ContentType.IMAGE_JPG);

            AmazonImage actualAmazonImage = amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile);

            assertEquals(testImageName, actualAmazonImage.imageName());
            assertEquals(testImageName, actualAmazonImage.imageKey());
        }
    }

    // #endregion

    // #region Input Validation Tests
    @Test
    void shouldThrowWhenFileIsEmpty() {

        MockMultipartFile mockMultipartFile = createMockFile(ContentType.IMAGE_JPG, "".getBytes());

        assertThrows(EmptyFileException.class, () ->
                amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile));
    }

    @Test
    void shouldThrowWhenFileIsNull() {

        assertThrows(EmptyFileException.class, () ->
                amazonImageServiceImpl.uploadImageToAmazon(null));
    }

    @Test
    void shouldThrowWhenFileIsTooLarge() {

        int MB = 1024 * 1024;
        byte[] content = new byte[MB];
        Arrays.fill(content, (byte) 0xff);

        MockMultipartFile mockMultipartFile = createMockFile(ContentType.IMAGE_JPG, content);

        assertThrows(FileSizeLimitExceededException.class, () ->
                amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile));
    }

    // #endregion

    // #region File type & content validation tests
    @Test
    void shouldThrowWhenContentTypeIsNotImage() {

        MockMultipartFile mockMultipartFile = createMockFile(ContentType.TEXT_PLAIN, testImageName.getBytes());

        assertThrows(NonImageFileException.class, () ->
                amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile));
    }

    @Test
    void shouldThrowWhenFileContentIsNotActualImage() {

        MockMultipartFile mockMultipartFile = createMockFile(ContentType.IMAGE_JPG, testImageName.getBytes());

        assertThrows(NonImageFileException.class, () ->
                amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile));
    }

    @Test
    void shouldThrowWhenImageHasInvalidExtension() throws IOException {

        String testImageName = "testImageInvalidExt.webp";
        MockMultipartFile mockMultipartFile = createMockFileFromResource(testImageName, ContentType.IMAGE_WEBP);

        assertThrows(InvalidImageExtensionException.class, () ->
                amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile));
    }

    // #endregion

    // #region Simulated internal failure scenarios
    @Test
    void shouldThrowWhenFileConversionFails() throws IOException {

        MockMultipartFile mockMultipartFile = createMockFileFromResource(testImageName, ContentType.IMAGE_JPG);

        assertThrows(UploadFailedException.class, () ->
                amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile));
    }

    // #endregion

    @Test
    void shouldThrowWhenUnableToConnectToAmazonS3OnUpload() throws IOException {

        // Amazon S3 setup
        when(amazonS3.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder().message("Unable to connect to S3").statusCode(500).build());

        try (MockedStatic<FileUtils> mockedStatic = mockStatic(FileUtils.class)) {

            mockedStatic.when(() -> FileUtils.generateFileName(any()))
                    .thenReturn(testImageName);

            MockMultipartFile mockMultipartFile = createMockFileFromResource(
                    testImageName, ContentType.IMAGE_JPG);

            assertThrows(S3ServiceException.class, () ->
                    amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile));
        }
    }

    @Test
    void shouldThrowWhenBadPutRequest() throws IOException {

        // Amazon S3 setup
        when(amazonS3.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(SdkClientException.builder().message("Bad Request").build());

        try (MockedStatic<FileUtils> mockedStatic = mockStatic(FileUtils.class)) {

            mockedStatic.when(() -> FileUtils.generateFileName(any()))
                    .thenReturn(testImageName);

            MockMultipartFile mockMultipartFile = createMockFileFromResource(
                    testImageName, ContentType.IMAGE_JPG);

            assertThrows(S3ClientException.class, () ->
                    amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile));
        }
    }

    @Test
    void shouldDeleteImageSuccessfully() {

        ArgumentCaptor<DeleteObjectRequest> captor = ArgumentCaptor.forClass(DeleteObjectRequest.class);

        amazonImageServiceImpl.removeImageFromAmazon(testImageName);

        verify(amazonS3).deleteObject(captor.capture());

        DeleteObjectRequest result = captor.getValue();
        assertEquals(bucketName, result.bucket());
        assertEquals(testImageName, result.key());
    }

    @Test
    void shouldThrowWhenUnableToConnectToAmazonOnDelete() {

        doThrow(S3Exception.builder().message("Unable to connect to S3").statusCode(500).build())
                .when(amazonS3).deleteObject(any(DeleteObjectRequest.class));

        assertThrows(S3ServiceException.class, () ->
                amazonImageServiceImpl.removeImageFromAmazon(testImageName));
    }

    @Test
    void shouldThrowWhenBadDeleteRequest() {

        doThrow(SdkClientException.builder().message("Bad Request").build())
                .when(amazonS3).deleteObject(any(DeleteObjectRequest.class));

        assertThrows(S3ClientException.class, () ->
                amazonImageServiceImpl.removeImageFromAmazon(testImageName));
    }

}
