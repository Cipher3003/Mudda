package com.mudda.backend.issue;

import com.mudda.backend.category.Category;
import com.mudda.backend.category.CategoryRepository;
import com.mudda.backend.comment.CommentService;
import com.mudda.backend.location.Location;
import com.mudda.backend.location.LocationMapper;
import com.mudda.backend.location.LocationRepository;
import com.mudda.backend.location.LocationDTO;
import com.mudda.backend.user.UserRepository;
import com.mudda.backend.utils.EntityValidator;
import com.mudda.backend.vote.Vote;
import com.mudda.backend.vote.VoteRepository;
import com.mudda.backend.vote.VoteService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final CommentService commentService;
    private final VoteRepository voteRepository;
    private final VoteService voteService;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;

    public IssueServiceImpl(IssueRepository issueRepository,
                            CommentService commentService,
                            VoteRepository voteRepository,
                            VoteService voteService,
                            UserRepository userRepository,
                            LocationRepository locationRepository,
                            CategoryRepository categoryRepository) {
        this.issueRepository = issueRepository;
        this.commentService = commentService;
        this.voteRepository = voteRepository;
        this.voteService = voteService;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.categoryRepository = categoryRepository;
    }

    // #region Queries (Read Operations)

    @Override
    public Page<IssueSummaryResponse> findAllIssues(IssueFilterRequest filterRequest, Pageable pageable, long userId) {

        List<Long> locationIds = locationRepository.findByCityAndState(filterRequest.city(), filterRequest.state())
                .stream()
                .map(Location::getLocationId)
                .toList();

        if (locationIds.isEmpty()) return Page.empty();

        Specification<Issue> specification = Specification.where(
                IssueSpecifications.containsText(filterRequest.search())
                        .and(IssueSpecifications.hasStatus(filterRequest.status()))
                        .and(IssueSpecifications.hasUserId(filterRequest.userId()))
                        .and(IssueSpecifications.hasCategoryId(filterRequest.categoryId()))
                        .and(IssueSpecifications.hasLocationIds(locationIds))
                        .and(IssueSpecifications.isUrgent(filterRequest.urgency()))
                        .and(IssueSpecifications.severityBetween(filterRequest.minSeverity(),
                                filterRequest.maxSeverity()))
                        .and(IssueSpecifications.createdAfter(filterRequest.createdAfter()))
                        .and(IssueSpecifications.createdBefore(filterRequest.createdBefore()))
        );

        Page<Issue> issuePage = issueRepository.findAll(specification, pageable);

        List<Long> issueIds = issuePage.getContent()
                .stream()
                .map(Issue::getId)
                .toList();

        Set<Long> issuesVotedByUser = voteRepository.findByUserIdAndIssueIdIn(userId, issueIds).stream()
                .map(Vote::getIssueId).collect(Collectors.toSet());

        return issuePage.map(issue -> {
                    long voteCount = voteRepository.countByIssueId(issue.getId());
                    return IssueMapper.toSummary(issue, voteCount, issuesVotedByUser.contains(issue.getId()));
                }
        );
    }

    @Override
    public Optional<IssueResponse> findById(long id, long userId) {
        Optional<Issue> optionalIssue = issueRepository.findById(id);
        if (optionalIssue.isEmpty()) return Optional.empty();

        Issue issue = optionalIssue.get();
        long voteCount = voteRepository.countByIssueId(issue.getId());
        boolean hasUserVoted = voteRepository.existsByIssueIdAndUserId(issue.getId(), userId);

        Optional<Location> location = locationRepository.findById(issue.getLocationId());
        if (location.isEmpty()) return Optional.empty();

        LocationDTO summary = LocationMapper.toSummary(location.get());

        Optional<Category> category = categoryRepository.findById(issue.getCategoryId());

        return category.map(categoryResponse ->
                IssueMapper.toResponse(issue, summary, categoryResponse.getName(), voteCount, hasUserVoted)
        );
    }

    // #endregion

    // #region Commands (Write Operations)

    @Transactional
    @Override
    public IssueResponse createIssue(CreateIssueRequest issueRequest) {
//        TODO: maybe remove unnecessary validation since fetching entities validates it
        validateReferences(issueRequest.userId(), issueRequest.locationId(), issueRequest.categoryId());

        Issue issue = IssueMapper.toIssue(issueRequest);

        Optional<Location> location = locationRepository.findById(issue.getLocationId());
        if (location.isEmpty())
            throw new IllegalArgumentException("Location ID for creating Issue not valid");

        Optional<Category> category = categoryRepository.findById(issueRequest.categoryId());
        if (category.isEmpty())
            throw new IllegalArgumentException("Category ID for creating Issue not valid");

        Issue saved = issueRepository.save(issue);
        return IssueMapper.toResponse(
                saved,
                LocationMapper.toSummary(location.get()),
                category.get().getName(),
                0,
                false
        );
    }

    @Transactional
    @Override
    public IssueUpdateResponse updateIssue(long id, UpdateIssueRequest issueRequest) {
        Issue existing = issueRepository.findById(id).
                orElseThrow(() -> notFound(id));

        existing.updateDetails(issueRequest.title(), issueRequest.description(), issueRequest.status());
        Issue updated = issueRepository.save(existing);
        return IssueMapper.toResponse(updated);
    }

    //    TODO: use delete flag to soft delete ?
    @Transactional
    @Override
    public void deleteIssue(long id) {
        Issue issue = issueRepository.findById(id).
                orElseThrow(() -> notFound(id));

        commentService.deleteAllCommentsByIssueId(issue.getId());
        voteService.deleteAllVotesByIssueId(issue.getId());
        issueRepository.deleteById(id);
    }

    //    TODO: use delete flag to soft delete ?
    @Transactional
    @Override
    public void deleteAllIssuesByUser(long userId) {
        List<Long> issueIds = issueRepository.findByUserId(userId)
                .stream()
                .map(Issue::getId)
                .toList();

        if (issueIds.isEmpty()) return;

        commentService.deleteAllCommentsByIssueIds(issueIds);
        voteService.deleteAllVotesByIssueIds(issueIds);
        issueRepository.deleteAllById(issueIds);
    }

    // #endregion

//    ------------------------------
//    Helpers
//    ------------------------------

    private void validateReferences(long userId, long locationId, long categoryId) {

        EntityValidator.validateExists(userRepository, userId, "User");
        EntityValidator.validateExists(locationRepository, locationId, "Location");
        EntityValidator.validateExists(categoryRepository, categoryId, "Category");
    }

    private EntityNotFoundException notFound(long id) {
        return new EntityNotFoundException("Issue not found with id: %d".formatted(id));
    }

}
