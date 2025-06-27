package com.mudda.backend.amazon.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mudda.backend.amazon.models.AmazonImage;

public interface AmazonImageRepository extends JpaRepository<AmazonImage, String> {
    
}
