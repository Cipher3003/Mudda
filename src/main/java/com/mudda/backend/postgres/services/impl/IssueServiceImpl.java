package com.mudda.backend.postgres.services.impl;

import com.mudda.backend.postgres.models.Issue;
import com.mudda.backend.postgres.models.IssueStatus;
import com.mudda.backend.postgres.repositories.IssueRepository;
import com.mudda.backend.postgres.services.IssueService;

import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import com.mudda.backend.postgres.services.CommentService;
import com.mudda.backend.postgres.services.VoteService;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final CommentService commentService;
    private final VoteService voteService;

    public IssueServiceImpl(IssueRepository issueRepository, CommentService commentService, VoteService voteService) {
        this.issueRepository = issueRepository;
        this.commentService = commentService;
        this.voteService = voteService;
    }

    @Override
    public List<Issue> findAllIssues() {
        return issueRepository.findAll();
    }

    @Override
    public Optional<Issue> findById(Long id) {
        return issueRepository.findById(id);
    }

    @Override
    public List<Issue> findByStatus(IssueStatus status) {
        return issueRepository.findByStatus(status);
    }

    @Override
    public List<Issue> findByUserId(Long userId) {
        return issueRepository.findByUserId(userId);
    }

    @Override
    public List<Issue> findByCategoryId(Long categoryId) {
        return issueRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Issue> findByLocation_CoordinatesNear(Point point, int distanceInKm) {
        return issueRepository.findByLocation_CoordinatesNear(point, distanceInKm);
    }

    @Override
    public Issue createIssue(Issue issue) {
        return issueRepository.save(issue);
    }

    @Override
    public Issue updateIssue(Long id, Issue issue) {
        Issue existing = issueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found with id " + id));

        existing.setTitle(issue.getTitle());
        existing.setDescription(issue.getDescription());
        existing.setStatus(issue.getStatus());
        existing.setMediaUrls(issue.getMediaUrls());
        existing.setUpdatedAt(issue.getUpdatedAt());
        existing.setLocationId(issue.getLocationId());
        existing.setCategoryId(issue.getCategoryId());

        return issueRepository.save(existing);
    }

    @Override
    public void deleteIssue(Long id) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found with id " + id));

        commentService.deleteAllCommentsByIssueId(issue.getId());
        voteService.deleteAllVotesByIssueId(issue.getId());
        // finally delete the issue
        issueRepository.deleteById(id);
    }
}
