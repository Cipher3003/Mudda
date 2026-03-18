/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : BatchImageUploadResponse
 * Author  : Vikas Kumar
 * Created : 03-02-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.media.dto;

import java.util.List;

public record BatchImageUploadResponse(
        int successCount,
        int failureCount,
        List<ImageUploadResponse> results
) {
}
