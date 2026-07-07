package com.mudda.backend.media;

import com.mudda.backend.exceptions.S3ClientException;
import com.mudda.backend.exceptions.S3ServiceException;
import com.mudda.backend.exceptions.UploadFailedException;
import com.mudda.backend.media.dto.ImageUploadResponse;
import com.mudda.backend.utils.MessageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MediaServiceImplTest {

    @Mock
    private S3Client amazonS3;

    @Mock
    private ImageValidator imageValidator;

    @Mock
    private MediaRepository mediaRepository;

    private AmazonMediaService amazonMediaService;

    @Mock
    private MediaHelperService mediaHelperService;

    @Mock
    private MessageUtil messageUtil;

    final String bucketName = "media-url-devbucket-2026";
    final String testImageName = "testImage.jpg";

    private MockMultipartFile createMockFileFromResource() throws IOException {
        return new MockMultipartFile("file", testImageName, ContentType.IMAGE_JPG.getValue(),
                new ClassPathResource(testImageName).getInputStream());
    }

    @BeforeEach
    void setUp() {
        amazonMediaService = new AmazonMediaService(bucketName, amazonS3, imageValidator,
                mediaRepository, mediaHelperService, messageUtil);
        ReflectionTestUtils.setField(amazonMediaService, "cdnOrigin", "https://cdn.example.com/");
    }

    // #region Success Case

    @Test
    void shouldUploadImageSuccessfully() throws IOException {
        String publicId = "abc123";
        String mediaKey = "images/abc123.jpg";
        MediaOwner owner = MediaOwner.ISSUE;
        MockMultipartFile mockFile = createMockFileFromResource();

        when(mediaHelperService.generatePublicId()).thenReturn(publicId);
        when(mediaHelperService.getMediaKey(owner, publicId, "jpg")).thenReturn(mediaKey);

        ImageUploadResponse response = amazonMediaService.uploadImage(new MediaFileUploadRequest(mockFile, owner));

        verify(imageValidator).validateImage(mockFile);
        verify(amazonS3).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(mediaHelperService).saveMedia(any(Media.class));

        assertEquals(testImageName, response.originalFileName());
        assertEquals(mediaKey, response.fileKey());
        assertEquals("https://cdn.example.com/".concat(mediaKey), response.url());
        assertEquals(UploadStatus.SUCCESS, response.status());
        assertNull(response.errorMessage());
    }

    // #endregion

    // #region Simulated internal failure scenarios
    @Test
    void shouldThrowWhenSourceStreamFails() throws IOException {

        MultipartFile multipartFile = mock(MultipartFile.class);
        MediaFileUploadRequest request = new MediaFileUploadRequest(multipartFile, MediaOwner.ISSUE);

        when(multipartFile.getOriginalFilename()).thenReturn(testImageName);
        when(multipartFile.getInputStream()).thenThrow(new IOException("Disk Error"));

        assertThrows(UploadFailedException.class, () -> amazonMediaService.uploadImage(request));
    }

    // #endregion

    @Test
    void shouldThrowWhenUnableToConnectToAmazonS3OnUpload() throws IOException {
        when(amazonS3.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder().message("Unable to connect to S3").statusCode(500).build());

        MultipartFile multipartFile = createMockFileFromResource();
        MediaFileUploadRequest request = new MediaFileUploadRequest(multipartFile, MediaOwner.ISSUE);

        assertThrows(S3ServiceException.class, () -> amazonMediaService.uploadImage(request));
    }

    @Test
    void shouldThrowWhenBadPutRequest() throws IOException {
        when(amazonS3.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(SdkClientException.builder().message("Bad Request").build());

        MultipartFile multipartFile = createMockFileFromResource();
        MediaFileUploadRequest request = new MediaFileUploadRequest(multipartFile, MediaOwner.ISSUE);

        assertThrows(S3ClientException.class, () -> amazonMediaService.uploadImage(request));
    }

    @Test
    void shouldThrowWhenMediaNotFound() {
        when(mediaRepository.markFailed(testImageName)).thenReturn(0);
        assertThrows(UploadFailedException.class, () -> amazonMediaService.deleteImage(testImageName));
    }

    @Test
    void shouldDeleteSuccessfully() {
        when(mediaRepository.markFailed(testImageName)).thenReturn(1);
        assertDoesNotThrow(() -> amazonMediaService.deleteImage(testImageName));
    }

}
