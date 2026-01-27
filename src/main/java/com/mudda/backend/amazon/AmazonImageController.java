package com.mudda.backend.amazon;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mudda.backend.exceptions.S3ClientException;
import com.mudda.backend.exceptions.S3ServiceException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @PostMapping
    public ResponseEntity<List<AmazonImage>> uploadImageToAmazon(@RequestParam List<MultipartFile> files) {
        log.info("Uploading image to Amazon");

        if (files.isEmpty()) {
            log.trace("No files to upload");
            return ResponseEntity.badRequest().build();
        }

        List<AmazonImage> uploadedImages = new ArrayList<>();

        for (MultipartFile file : files)
            uploadedImages.add(amazonImageService.uploadImageToAmazon(file));

        return ResponseEntity.status(HttpStatus.CREATED).body(uploadedImages);

    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<Void> deleteImage(@PathVariable String fileName) {
        log.info("Deleting image with name {}", fileName);

        try {
            amazonImageService.removeImageFromAmazon(fileName);
            return ResponseEntity.noContent().build();
//            TODO: use global exception handler
        } catch (S3ServiceException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (S3ClientException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // endregion
}