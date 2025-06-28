package com.mudda.backend.amazon.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import jakarta.annotation.PostConstruct;

@Service
public class AmazonClientService {

    private AmazonS3 amazonS3;

    @Value("${amazon.s3.region}")
    private String region;

    @Value("${amazon.s3.bucket-name}")
    private String bucketName;

    @Value("${amazon.s3.access-key}")
    private String accessKey;

    @Value("${amazon.s3.secret-key}")
    private String secretKey;

    public AmazonS3 getAmazonS3() {
        return amazonS3;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getUrl() {
        // Construct S3 public URL
        return String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);
    }

    @PostConstruct
    private void init() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        this.amazonS3 = AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

}
