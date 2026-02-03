package com.mudda.backend.amazon;

public record ImageUploadResponse(
        String originalFileName,
        String fileKey,
        UploadStatus status,
        String errorMessage
) {
}
