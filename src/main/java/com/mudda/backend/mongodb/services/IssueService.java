package com.mudda.backend.mongodb.services;

import com.mudda.backend.mongodb.models.Issue;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface IssueService {
    Issue createIssue(Issue issue);
    Optional<Issue> findIssueById(ObjectId id);
    List<Issue> findAllIssues();
    void deleteIssue(ObjectId id);
}
