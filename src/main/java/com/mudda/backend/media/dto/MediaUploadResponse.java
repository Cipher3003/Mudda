/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : MediaUploadResponse
 * Author  : Vikas Kumar
 * Created : 16-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.media.dto;

public record MediaUploadResponse(
        String id,
        String uploadUrl
) {
}
