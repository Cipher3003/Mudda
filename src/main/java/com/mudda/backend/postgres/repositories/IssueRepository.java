package com.mudda.backend.postgres.repositories;

import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.mudda.backend.postgres.models.Issue;
import com.mudda.backend.postgres.models.IssueStatus;

import java.util.List;

@Repository
public interface IssueRepository extends MongoRepository<Issue, Long> {

    List<Issue> findByStatus(IssueStatus status);

    List<Issue> findByUserId(Long userId);

    List<Issue> findByCategoryId(Long categoryId);

    List<Issue> findByLocation_CoordinatesNear(Point point, int distance);

}