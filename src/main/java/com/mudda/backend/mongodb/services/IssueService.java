package com.mudda.backend.mongodb.services;

import com.mudda.backend.mongodb.models.Issue;
import com.mudda.backend.mongodb.models.IssueStatus;
import org.bson.types.ObjectId;
import org.springframework.data.geo.Point;

import java.util.List;
import java.util.Optional;

public interface IssueService {
    Issue createIssue(Issue issue);
    Optional<Issue> findIssueById(ObjectId id);
    List<Issue> findAllIssues();
    void deleteIssue(ObjectId id);
    List<Issue> findByStatus(IssueStatus status);
    List<Issue> findIssueByUserId(Long user_id);
    List<Issue> findIssueByCategoryId(Long category_id);
    List<Issue> findByLocation_CoordinatesNear(Point point, int distance);

}
