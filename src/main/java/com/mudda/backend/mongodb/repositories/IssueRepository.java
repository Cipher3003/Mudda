package com.mudda.backend.mongodb.repositories;


import com.mudda.backend.mongodb.models.Issue;
import com.mudda.backend.mongodb.models.IssueStatus;
import org.bson.types.ObjectId;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IssueRepository extends MongoRepository<Issue, ObjectId> {
    List<Issue> findByStatus(IssueStatus status);
    List<Issue> findIssueByUserName(String user_name);
    List<Issue> findIssueByCategoryId(Long category_id);
    List<Issue> findByLocation_CoordinatesNear(Point point, int distance);

}