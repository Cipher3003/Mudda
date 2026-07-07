/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : MediaUploadRequest
 * Author  : Vikas Kumar
 * Created : 16-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.media.dto;

import com.mudda.backend.media.MediaOwner;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MediaUploadRequest(
        @NotNull @NotBlank String fileName,
        @NotNull @NotBlank String contentType,
        @NotNull @Positive Integer position,
        @NotNull MediaOwner owner
) {
}
