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
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1/amazon/images")
public class AmazonImageController {

    private AmazonImageService amazonImageService;

    public AmazonImageController(AmazonImageService amazonImageService) {
        this.amazonImageService = amazonImageService;
    }

    @GetMapping("")
    public ResponseEntity<List<String>> getBucketContents() {
        List<String> bucketContetList = amazonImageService.getBucketContents();
        return ResponseEntity.ok(bucketContetList);
    }

    @PostMapping
    public ResponseEntity<AmazonImage> uploadImageToAmazon(@RequestParam MultipartFile file) {

        AmazonImage amazonImage = amazonImageService.uploadImageToAmazon(file);
        return new ResponseEntity<AmazonImage>(amazonImage, HttpStatus.CREATED);

    }
}