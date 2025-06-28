package com.mudda.backend.amazon.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mudda.backend.amazon.models.AmazonImage;

@Repository
public interface AmazonImageRepository extends JpaRepository<AmazonImage, String> {

}
