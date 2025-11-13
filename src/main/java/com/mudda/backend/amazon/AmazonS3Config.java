package com.mudda.backend.amazon;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AmazonS3Config {
    @Getter
    private AmazonS3 amazonS3;

    @Getter
    @Value("${amazon.s3.region}")
    private String region;

    @Value("${amazon.s3.access-key}")
    private String accessKey;

    @Value("${amazon.s3.secret-key}")
    private String secretKey;

    @Bean
    AmazonS3 s3() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        return AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

}
