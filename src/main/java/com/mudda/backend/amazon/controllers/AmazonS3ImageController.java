package com.mudda.backend.amazon.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mudda.backend.amazon.models.AmazonImage;
import com.mudda.backend.amazon.services.AmazonS3ImageService;
import org.springframework.web.bind.annotation.GetMapping;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/amazon/images")
public class AmazonS3ImageController {

    private final AmazonS3ImageService amazonS3ImageService;

    public AmazonS3ImageController(AmazonS3ImageService amazonS3ImageService) {
        this.amazonS3ImageService = amazonS3ImageService;
    }

    /**
     * Uploads an image to Amazon S3.
     *
     * @param file the image file to upload
     * @return ResponseEntity containing the uploaded AmazonImage object
     */
    @PostMapping("/upload")
    public ResponseEntity<AmazonImage> uploadImage(@RequestParam MultipartFile file) {
        AmazonImage uploadedImage = amazonS3ImageService.uploadImageToAmazon(file);
        return ResponseEntity.ok(uploadedImage);
    }

    @PostMapping("/check-image-exists-in-database")
    public ResponseEntity<Boolean> checkImageExistsInDatabase(@RequestParam String fileName) {
        boolean exists = amazonS3ImageService.checkImageExists(fileName);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/check-image-exists-in-cloud")
    public ResponseEntity<Boolean> checkImageExistsInCloud(@RequestParam String fileName) {
        boolean exists = amazonS3ImageService.checkImageUpload(fileName);
        return ResponseEntity.ok(exists);
    }

    @GetMapping()
    public ResponseEntity<List<String>> getBucketContents() {
        List<String> bucketContentList = amazonS3ImageService.listBucketContents();
        return ResponseEntity.ok(bucketContentList);
    }

}
