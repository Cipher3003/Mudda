package com.mudda.backend.amazon.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mudda.backend.amazon.models.AmazonImage;

public interface AmazonImageService {

    public AmazonImage uploadImageToAmazon(MultipartFile file);

    public List<String> getBucketContents();

    public void removeImageFromAmazon(String imageFileName);

}
