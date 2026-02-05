package com.mudda.backend.amazon.services.impl;

import com.mudda.backend.amazon.ImageUploadResponse;
import com.mudda.backend.amazon.AmazonImageServiceImpl;
import com.mudda.backend.amazon.ContentType;
import com.mudda.backend.amazon.ImageValidator;
import com.mudda.backend.exceptions.*;
import com.mudda.backend.utils.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AmazonImageServiceImplTest {

    @Mock
    private S3Client amazonS3;

    @Mock
    private ImageValidator imageValidator;

    private AmazonImageServiceImpl amazonImageServiceImpl;

    final String bucketName = "media-url-devbucket-2026";
    final String testImageName = "testImage.jpg";

    private MockMultipartFile createMockFileFromResource() throws IOException {
        return new MockMultipartFile("file", testImageName, ContentType.IMAGE_JPG.getValue(),
                new ClassPathResource(testImageName).getInputStream());
    }

    @BeforeEach
    void setUp() {
        // AmazonImageServiceImpl setup
        amazonImageServiceImpl = new AmazonImageServiceImpl(bucketName, amazonS3, imageValidator);
    }

    // #region Success Case
    @Test
    void shouldUploadImageSuccessfully() throws IOException {

        try (MockedStatic<FileUtils> mockedStatic = mockStatic(FileUtils.class)) {

            mockedStatic.when(() -> FileUtils.generateFileName(any())).thenReturn(testImageName);
            MockMultipartFile mockMultipartFile = createMockFileFromResource();

            ImageUploadResponse response = amazonImageServiceImpl.uploadImageToAmazon(mockMultipartFile);

            verify(imageValidator).validateImage(mockMultipartFile);

            verify(amazonS3).putObject(any(PutObjectRequest.class), any(RequestBody.class));

            assertEquals(testImageName, response.fileKey());
        }
    }

    // #endregion

    // #region Simulated internal failure scenarios
    @Test
    void shouldThrowWhenSourceStreamFails() throws IOException {

        MultipartFile multipartFile = mock(MultipartFile.class);

        when(multipartFile.getOriginalFilename()).thenReturn(testImageName);
        when(multipartFile.getInputStream()).thenThrow(new IOException("Disk Error"));

        assertThrows(UploadFailedException.class, () -> amazonImageServiceImpl.uploadImageToAmazon(multipartFile));
    }

    // #endregion

    @Test
    void shouldThrowWhenUnableToConnectToAmazonS3OnUpload() throws IOException {

        // Amazon S3 setup
        when(amazonS3.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder().message("Unable to connect to S3").statusCode(500).build());

        try (MockedStatic<FileUtils> mockedStatic = mockStatic(FileUtils.class)) {

            mockedStatic.when(() -> FileUtils.generateFileName(any())).thenReturn(testImageName);

            MockMultipartFile mockMultipartFile = createMockFileFromResource();

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

            MockMultipartFile mockMultipartFile = createMockFileFromResource();

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

        assertThrows(S3ServiceException.class, () -> amazonImageServiceImpl.removeImageFromAmazon(testImageName));
    }

    @Test
    void shouldThrowWhenBadDeleteRequest() {

        doThrow(SdkClientException.builder().message("Bad Request").build())
                .when(amazonS3).deleteObject(any(DeleteObjectRequest.class));

        assertThrows(S3ClientException.class, () -> amazonImageServiceImpl.removeImageFromAmazon(testImageName));
    }

}
