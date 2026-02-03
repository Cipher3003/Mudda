package com.mudda.backend.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import org.springframework.web.multipart.MultipartFile;

public class FileUtils {

    private final static String CDN_URL = "https://cdn.mudda.dev/";

    public static File convertMultipartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fileOutputStream = new FileOutputStream(convertedFile);
        fileOutputStream.write(file.getBytes());
        fileOutputStream.close();
        return convertedFile;
    }

    public static String getPublicUrl(String fileName) {
        return String.format("%s%s", CDN_URL, fileName);
    }

    public static String generateFileName(MultipartFile file) {
        return new Date().getTime() + "-"
                + Objects.requireNonNull(file.getOriginalFilename())
                .replace(" ", "_");
    }

    private String getFileUrl(String bucketName, String region) {
        return String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);
    }
}
