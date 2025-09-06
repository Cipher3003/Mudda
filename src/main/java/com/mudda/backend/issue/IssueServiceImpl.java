package com.mudda.backend.issue;

import com.mudda.backend.category.CategoryService;
import com.mudda.backend.postgres.services.LocationService;
import com.mudda.backend.postgres.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;
import org.springframework.data.jpa.domain.Specification;
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
    private final UserService userService;
    private final LocationService locationService;
    private final CategoryService categoryService;
    private final IssueMapper issueMapper;

    public IssueServiceImpl(IssueRepository issueRepository,
                            CommentService commentService,
                            VoteService voteService,
                            UserService userService,
                            LocationService locationService,
                            CategoryService categoryService,
                            IssueMapper issueMapper) {
        this.issueRepository = issueRepository;
        this.commentService = commentService;
        this.voteService = voteService;
        this.userService = userService;
        this.locationService = locationService;
        this.categoryService = categoryService;
        this.issueMapper = issueMapper;
    }

//    ------------------------------
//     Read Operations (queries)
//    ------------------------------

    @Override
    public Page<IssueSummaryResponse> findAllIssues(IssueFilterRequest filterRequest, Pageable pageable) {

        Specification<Issue> specification = Specification.where(
                IssueSpecifications.containsText(filterRequest.search())
                        .and(IssueSpecifications.hasStatus(filterRequest.status()))
                        .and(IssueSpecifications.hasUserId(filterRequest.userId()))
                        .and(IssueSpecifications.hasCategoryId(filterRequest.categoryId()))
                        .and(IssueSpecifications.hasLocationId(filterRequest.locationId()))
                        .and(IssueSpecifications.isUrgent(filterRequest.urgency()))
                        .and(IssueSpecifications.severityBetween(filterRequest.minSeverity(),
                                filterRequest.maxSeverity()))
                        .and(IssueSpecifications.createdAfter(filterRequest.createdAfter()))
                        .and(IssueSpecifications.createdBefore(filterRequest.createdBefore()))
        );

        Page<Issue> issuePage = issueRepository.findAll(specification, pageable);

        return issuePage.map(issueMapper::toSummary);
    }

    @Override
    public Optional<IssueResponse> findById(Long id) {
        return issueRepository.findProjectedById(id);
    }

    @Override
    public List<IssueSummaryResponse> findByStatus(IssueStatus status) {
        return issueRepository.findByStatus(status);
    }

    @Override
    public List<IssueSummaryResponse> findByUserId(Long userId) {
        return issueRepository.findByUserId(userId);
    }

    @Override
    public List<IssueSummaryResponse> findByCategoryId(Long categoryId) {
        return issueRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Issue> findByLocation_CoordinatesNear(Point point, int distance) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByLocation_CoordinatesNear'");
    }

//    ------------------------------
//    Write Operations (commands)
//    ------------------------------

    @Override
    public IssueResponse createIssue(CreateIssueRequest issueRequest) {
        validateReferences(issueRequest.userId(), issueRequest.locationId(), issueRequest.categoryId());

        Issue issue = new Issue(
                issueRequest.title(),
                issueRequest.description(),
                issueRequest.userId(),
                issueRequest.locationId(),
                issueRequest.categoryId(),
                issueRequest.mediaUrls()
        );

        Issue saved = issueRepository.save(issue);
        return issueMapper.toResponse(saved);
    }

    @Override
    public IssueResponse updateIssue(Long id, UpdateIssueRequest issueRequest) {
        Issue existing = issueRepository.findById(id).
                orElseThrow(() -> notFound("Issue", id));

        existing.updateDetails(issueRequest.title(), issueRequest.description(), issueRequest.status());

        Issue updated = issueRepository.save(existing);
        return issueMapper.toResponse(updated);
    }

    @Override
    public void deleteIssue(Long id) {
        Issue issue = issueRepository.findById(id).
                orElseThrow(() -> notFound("Issue", id));

        commentService.deleteAllCommentsByIssueId(issue.getId());
        voteService.deleteAllVotesByIssueId(issue.getId());
        issueRepository.deleteById(id);
    }

//    ------------------------------
//    Helpers
//    ------------------------------

    private void validateReferences(Long userId, Long locationId, Long categoryId) {

        if (userService.findUserById(userId).isEmpty())
            throw notFound("User", userId);
        if (locationService.findLocationById(locationId).isEmpty())
            throw notFound("Location", locationId);
        if (categoryService.findCategoryById(categoryId).isEmpty())
            throw notFound("Category", categoryId);
    }

    private EntityNotFoundException notFound(String entity, Long id) {
        return new EntityNotFoundException(entity + " not found with id " + id);
    }

}
