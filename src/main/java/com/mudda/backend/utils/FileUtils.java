package com.mudda.backend.utils;

import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.Objects;

public class FileUtils {

    public static String generateFileName(MultipartFile file) {
        return new Date().getTime() + "-"
                + Objects.requireNonNull(file.getOriginalFilename())
                .replace(" ", "_");
    }
}
