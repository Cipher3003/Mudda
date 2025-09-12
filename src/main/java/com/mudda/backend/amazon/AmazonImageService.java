package com.mudda.backend.amazon;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface AmazonImageService {

    public AmazonImage uploadImageToAmazon(MultipartFile file);

    public List<String> getBucketContents();

    public void removeImageFromAmazon(String imageFileName);

}
