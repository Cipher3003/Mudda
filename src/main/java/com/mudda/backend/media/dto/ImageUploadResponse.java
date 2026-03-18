package com.mudda.backend.media.dto;

import com.mudda.backend.media.UploadStatus;

public record ImageUploadResponse(
        String originalFileName,
        String fileKey,
        String url,
        UploadStatus status,
        String errorMessage
) {
}
