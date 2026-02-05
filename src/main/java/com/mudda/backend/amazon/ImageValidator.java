/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : ImageValidator
 * Author  : Vikas Kumar
 * Created : 03-02-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.amazon;

import com.mudda.backend.exceptions.EmptyFileException;
import com.mudda.backend.exceptions.FileSizeLimitExceededException;
import com.mudda.backend.exceptions.InvalidImageExtensionException;
import com.mudda.backend.exceptions.NonImageFileException;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
public class ImageValidator {

    private final Tika tika;

    private static final List<String> VALID_EXTENSIONS = List.of("jpeg", "png", "jpg");
    private static final long MAX_SIZE_BYTES = 1024 * 1024;
    private static final int MAX_SIZE_MB = 1;

    public ImageValidator(Tika tika) {
        this.tika = tika;
    }

    public void validateImage(MultipartFile multipartFile) {
        // Check if file is empty or null
        if (multipartFile == null || multipartFile.isEmpty()) throw new EmptyFileException();

        // Check if file size exceeds maximum size (1MB default)
        if (multipartFile.getSize() >= MAX_SIZE_BYTES) throw new FileSizeLimitExceededException(MAX_SIZE_MB);

        // Check if file extensions are valid else throw InvalidImageExtensionException
        String fileExtension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        if (fileExtension == null ||
                VALID_EXTENSIONS
                        .stream()
                        .noneMatch(ext -> ext.equalsIgnoreCase(fileExtension)))
            throw new InvalidImageExtensionException(String.join(", ", VALID_EXTENSIONS));

        try {
            String detectedType = tika.detect(multipartFile.getInputStream());

            if (!detectedType.startsWith("image/")) throw new NonImageFileException();

        } catch (IOException e) {
            throw new NonImageFileException();
        }
    }
}
