package com.mudda.backend.amazon;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/amazon/images")
public class AmazonImageController {

    private final AmazonImageService amazonImageService;

    public AmazonImageController(AmazonImageService amazonImageService) {
        this.amazonImageService = amazonImageService;
    }

    // ----------- PUBLIC READ -----------------
    // region Queries (Read Operations)

    @GetMapping
    public ResponseEntity<List<String>> getBucketContents() {
        log.info("Getting aws bucket contents");
        List<String> bucketContetList = amazonImageService.getBucketContents();
        return ResponseEntity.ok(bucketContetList);
    }

    // endregion

    // ----------- AUTH COMMANDS -----------------
    // region Commands (Write Operations)

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponse> uploadImageToAmazon(@RequestParam MultipartFile file) {
        if (file == null || file.isEmpty()) return ResponseEntity.badRequest().build();

        return ResponseEntity.status(HttpStatus.CREATED).body(amazonImageService.uploadImageToAmazon(file));
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
        return ResponseEntity.status(HttpStatus.CREATED).body(amazonImageService.uploadImagesToAmazon(files));

    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<Void> deleteImage(@PathVariable String fileName) {
        log.info("Deleting image with name {}", fileName);

        amazonImageService.removeImageFromAmazon(fileName);
        return ResponseEntity.noContent().build();
    }

    // endregion
}