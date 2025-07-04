package com.mudda.backend.amazon.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mudda.backend.amazon.models.AmazonImage;
import com.mudda.backend.amazon.services.AmazonImageService;
import com.mudda.backend.exceptions.S3ClientException;
import com.mudda.backend.exceptions.S3ServiceException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/amazon/images")
public class AmazonImageController {

    private AmazonImageService amazonImageService;

    public AmazonImageController(AmazonImageService amazonImageService) {
        this.amazonImageService = amazonImageService;
    }

    @GetMapping
    public ResponseEntity<List<String>> getBucketContents() {
        List<String> bucketContetList = amazonImageService.getBucketContents();
        return ResponseEntity.ok(bucketContetList);
    }

    @PostMapping
    public ResponseEntity<AmazonImage> uploadImageToAmazon(@RequestParam MultipartFile file) {

        AmazonImage amazonImage = amazonImageService.uploadImageToAmazon(file);
        return new ResponseEntity<AmazonImage>(amazonImage, HttpStatus.CREATED);

    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<Void> deleteImage(@PathVariable String fileName) {
        // TODO: handle exception
        try {
            amazonImageService.removeImageFromAmazon(fileName);
            return ResponseEntity.noContent().build();
        } catch (S3ServiceException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (S3ClientException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}