package com.mudda.backend.amazon;

public record ImageUploadResponse(
        String originalFileName,
        String fileKey,
        String url,
        UploadStatus status,
        String errorMessage
) {
}
