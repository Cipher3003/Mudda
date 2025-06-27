package com.mudda.backend.mongodb.services.impl;

import com.mudda.backend.mongodb.models.Issue;
import com.mudda.backend.mongodb.models.IssueStatus;
import com.mudda.backend.mongodb.repositories.IssueRepository;
import com.mudda.backend.mongodb.services.IssueService;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IssueServiceImpl implements IssueService {

    @Autowired
    private IssueRepository issueRepository;

    @Override
    public Issue createIssue(Issue issue) {
        return issueRepository.save(issue);
    }

    @Override
    public Optional<Issue> findIssueById(ObjectId id) {
        return issueRepository.findById(id);
    }

    @Override
    public List<Issue> findAllIssues() {
        return issueRepository.findAll();
    }

    @Override
    public void deleteIssue(ObjectId id) {
        issueRepository.deleteById(id);
    }

    public List<Issue> findByStatus(IssueStatus status) {
        return issueRepository.findByStatus(status);
    }

    public List<Issue> findIssueByUserId(Long user_id) {
        return issueRepository.findIssueByUserId(user_id);
    }

    public List<Issue> findIssueByCategoryId(Long category_id){
        return issueRepository.findIssueByCategoryId(category_id);
    }

    public List<Issue> findByLocation_CoordinatesNear(Point point, int distanceInKm) {
        return issueRepository.findByLocation_CoordinatesNear(point, distanceInKm);
    }
}
