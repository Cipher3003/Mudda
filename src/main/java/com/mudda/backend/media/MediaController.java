package com.mudda.backend.media;

import com.mudda.backend.media.dto.BatchImageUploadResponse;
import com.mudda.backend.media.dto.ImageUploadResponse;
import com.mudda.backend.media.dto.MediaUploadRequest;
import com.mudda.backend.media.dto.MediaUploadResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    // ----------- PUBLIC READ -----------------
    // region Queries (Read Operations)

    @GetMapping
    public ResponseEntity<List<String>> getMediaImages() {
        log.info("Getting aws bucket contents");
        List<String> bucketContetList = mediaService.getImages();
        return ResponseEntity.ok(bucketContetList);
    }

    // endregion

    // ----------- AUTH COMMANDS -----------------
    // region Commands (Write Operations)

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponse> uploadImageToAmazon(@RequestParam MultipartFile file) {
        if (file == null || file.isEmpty()) return ResponseEntity.badRequest().build();

        return ResponseEntity.status(HttpStatus.CREATED).body(mediaService.uploadImage(file));
    }

    @PostMapping(
            value = "/batch",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<BatchImageUploadResponse> uploadImagesToAmazon(@RequestParam List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            log.trace("No files to upload");
            return ResponseEntity.badRequest().build();
        }

        log.info("Uploading batch images to Amazon");
        return ResponseEntity.status(HttpStatus.CREATED).body(mediaService.uploadImages(files));
    }

    @PostMapping("/init")
    public ResponseEntity<MediaUploadResponse> initUpload(@RequestBody @Valid MediaUploadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediaService.initUpload(request));
    }

    @PostMapping("/init/batch")
    public ResponseEntity<List<MediaUploadResponse>> initUpload(
            @RequestBody @Size(min = 1, max = 5) List<MediaUploadRequest> requests
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediaService.initUploads(requests));
    }

    @PutMapping("/{publicId}/complete")
    public ResponseEntity<Void> completeUpload(@PathVariable String publicId) {
        mediaService.completeUpload(publicId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<Void> deleteImage(@PathVariable String publicId) {
        log.info("Deleting image with name {}", publicId);

        mediaService.deleteImage(publicId);
        return ResponseEntity.noContent().build();
    }

    // endregion
}