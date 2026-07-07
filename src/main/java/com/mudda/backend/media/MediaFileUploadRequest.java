/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : MediaFileUploadRequest
 * Author  : Vikas Kumar
 * Created : 6/4/2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.media;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record MediaFileUploadRequest(
        @NotNull MultipartFile multipartFile,
        @NotNull MediaOwner owner
) {
}
