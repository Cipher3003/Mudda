/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : ImageValidatorTest
 * Author  : Vikas Kumar
 * Created : 03-02-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.amazon;

import com.mudda.backend.exceptions.EmptyFileException;
import com.mudda.backend.exceptions.FileSizeLimitExceededException;
import com.mudda.backend.exceptions.InvalidImageExtensionException;
import com.mudda.backend.exceptions.NonImageFileException;
import org.apache.tika.Tika;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageValidatorTest {

    @Mock
    private Tika tika;

    @InjectMocks
    private ImageValidator imageValidator;

    private MockMultipartFile createMockFile(String fileName, ContentType fileType, byte[] content) {
        return new MockMultipartFile("file", fileName, fileType.getValue(), content);
    }

    @Test
    void shouldThrowWhenFileIsEmpty() {
        MockMultipartFile mockMultipartFile = createMockFile("test.jpg", ContentType.IMAGE_JPG, "".getBytes());
        assertThrows(EmptyFileException.class, () -> imageValidator.validateImage(mockMultipartFile));
    }

    @Test
    void shouldThrowWhenFileIsNull() {
        assertThrows(EmptyFileException.class, () -> imageValidator.validateImage(null));
    }

    @Test
    void shouldThrowWhenFileIsTooLarge() {

        byte[] largeContent = new byte[(1024 * 1024) + 1];  // 1MB + 1 byte
        MockMultipartFile mockMultipartFile = createMockFile("large.jpg", ContentType.IMAGE_JPG, largeContent);

        assertThrows(FileSizeLimitExceededException.class, () -> imageValidator.validateImage(mockMultipartFile));
    }

    @Test
    void shouldThrowWhenImageHasInvalidExtension() {

        MockMultipartFile mockMultipartFile = createMockFile(
                "test.gif", ContentType.TEXT_PLAIN, "data".getBytes());

        assertThrows(InvalidImageExtensionException.class, () -> imageValidator.validateImage(mockMultipartFile));
    }

    @Test
    void shouldThrowWhenTikaDetectsNonImage() throws IOException {
        MockMultipartFile mockMultipartFile = createMockFile(
                "fake.jpg", ContentType.IMAGE_JPG, "virus".getBytes());

        when(tika.detect(any(InputStream.class))).thenReturn("text/plain");

        assertThrows(NonImageFileException.class, () -> imageValidator.validateImage(mockMultipartFile));
    }

    @Test
    void shouldPassForValidImage() throws IOException {
        MockMultipartFile file = createMockFile("real.jpg", ContentType.IMAGE_JPG, "real_image_data".getBytes());
        when(tika.detect(any(InputStream.class))).thenReturn("image/jpg");

        assertDoesNotThrow(() -> imageValidator.validateImage(file));
    }

}