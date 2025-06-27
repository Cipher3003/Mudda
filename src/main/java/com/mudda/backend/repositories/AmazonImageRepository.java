package com.mudda.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mudda.backend.models.AmazonImage;

public interface AmazonImageRepository extends JpaRepository<AmazonImage, String> {
    
}
