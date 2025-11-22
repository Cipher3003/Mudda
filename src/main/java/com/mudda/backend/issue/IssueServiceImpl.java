package com.mudda.backend.issue;

import com.mudda.backend.category.Category;
import com.mudda.backend.category.CategoryRepository;
import com.mudda.backend.comment.CommentService;
import com.mudda.backend.location.Location;
import com.mudda.backend.location.LocationDTO;
import com.mudda.backend.location.LocationMapper;
import com.mudda.backend.location.LocationRepository;
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
import java.util.stream.IntStream;

@Service
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final CommentService commentService;
    private final VoteRepository voteRepository;
    private final VoteService voteService;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;

    public IssueServiceImpl(IssueRepository issueRepository,
            CommentService commentService,
            VoteRepository voteRepository,
            VoteService voteService,
            LocationRepository locationRepository,
            CategoryRepository categoryRepository) {
        this.issueRepository = issueRepository;
        this.commentService = commentService;
        this.voteRepository = voteRepository;
        this.voteService = voteService;
        this.locationRepository = locationRepository;
        this.categoryRepository = categoryRepository;
    }

    // #region Queries (Read Operations)

    @Override
    public Page<IssueSummaryResponse> findAllIssues(IssueFilterRequest filterRequest, Pageable pageable, Long userId) {

        List<Long> locationIds = locationRepository.findByCityAndState(filterRequest.city(), filterRequest.state())
                .stream()
                .map(Location::getLocationId)
                .toList();

        Specification<Issue> specification = IssueSpecifications
                .containsText(filterRequest.search())
                .and(IssueSpecifications.hasStatus(filterRequest.status()))
                .and(IssueSpecifications.hasUserId(filterRequest.userId()))
                .and(IssueSpecifications.hasCategoryId(filterRequest.categoryId()))
                .and(IssueSpecifications.hasLocationIds(locationIds))
                .and(IssueSpecifications.isUrgent(filterRequest.urgency()))
                .and(IssueSpecifications.severityBetween(filterRequest.minSeverity(), filterRequest.maxSeverity()))
                .and(IssueSpecifications.createdAfter(filterRequest.createdAfter()))
                .and(IssueSpecifications.createdBefore(filterRequest.createdBefore()));

        Page<Issue> issuePage = issueRepository.findAll(specification, pageable);

        List<Long> issueIds = issuePage.getContent()
                .stream()
                .map(Issue::getId)
                .toList();

        if (userId != null) {

            // TODO: maybe use Vote entity's own check vote casted by user to filter this
            // set even more
            Set<Long> issuesVotedByUser = voteRepository.findByUserIdAndIssueIdIn(userId, issueIds).stream()
                    .map(Vote::getIssueId).collect(Collectors.toSet());

            // TODO: canUserVote is recommended to be always true but seems sus
            return issuePage.map(issue -> {
                long voteCount = voteRepository.countByIssueId(issue.getId());
                return IssueMapper.toSummary(
                        issue, voteCount,
                        issuesVotedByUser.contains(issue.getId()), true);
            });
        }

        return issuePage.map(issue -> {
            long voteCount = voteRepository.countByIssueId(issue.getId());
            return IssueMapper.toSummary(
                    issue, voteCount,
                    false, false);
        });
    }

    @Override
    public Optional<IssueResponse> findById(long id, Long userId) {
        Optional<Issue> optionalIssue = issueRepository.findById(id);
        if (optionalIssue.isEmpty())
            return Optional.empty();

        Issue issue = optionalIssue.get();
        long voteCount = voteRepository.countByIssueId(issue.getId());
        boolean hasUserVoted = voteRepository.existsByIssueIdAndUserId(issue.getId(), userId);

        Optional<Location> location = locationRepository.findById(issue.getLocationId());
        if (location.isEmpty())
            return Optional.empty();

        LocationDTO summary = LocationMapper.toSummary(location.get());

        Optional<Category> category = categoryRepository.findById(issue.getCategoryId());

        // TODO: canUserVote is recommended to be always true but seems sus
        return category.map(categoryResponse -> IssueMapper.toResponse(
                issue, summary, categoryResponse.getName(), voteCount, hasUserVoted,
                true, true,
                issue.getUserId().equals(userId),
                issue.getUserId().equals(userId)));
    }

    // #endregion

    // #region Commands (Write Operations)

    @Transactional
    @Override
    public IssueResponse createIssue(Long userId, CreateIssueRequest issueRequest) {

        // TODO: change the exception to custom ?
        if (userId == null)
            throw new IllegalArgumentException("UserId not correct, Login with proper credentials");

        // TODO: maybe remove unnecessary validation since fetching entities validates
        // it
        validateReferences(issueRequest.locationId(), issueRequest.categoryId());

        Issue issue = IssueMapper.toIssue(userId, issueRequest);

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
                false,
                true, true, true, true);
    }

    @Transactional
    @Override
    public List<Long> createIssues(List<Long> userIds, List<CreateIssueRequest> issueRequests) {
        List<Issue> issues = issueRepository.saveAll(
                IntStream
                        .range(0, userIds.size())
                        .mapToObj(index ->
                                IssueMapper.toIssue(userIds.get(index), issueRequests.get(index)))
                        .toList());

        return issues.stream().map(Issue::getId).toList();
    }

    @Transactional
    @Override
    public IssueUpdateResponse updateIssue(long id, Long userId, UpdateIssueRequest issueRequest) {
        Issue existing = issueRepository.findById(id).orElseThrow(() -> notFound(id));

        // TODO: change the exception to custom ?
        if (userId == null)
            throw new IllegalArgumentException("UserId not correct, Login with proper credentials");

        existing.updateDetails(issueRequest.title(), issueRequest.description(), issueRequest.status());
        Issue updated = issueRepository.save(existing);
        return IssueMapper.toResponse(updated);
    }

    // TODO: use delete flag to soft delete ?
    @Transactional
    @Override
    public void deleteIssue(long id, Long userId) {
        Issue issue = issueRepository.findById(id).orElseThrow(() -> notFound(id));

        // TODO: change the exception to custom ?
        if (userId == null)
            throw new IllegalArgumentException("UserId not correct, Login with proper credentials");

        commentService.deleteAllCommentsByIssueId(issue.getId());
        voteService.deleteAllVotesByIssueId(issue.getId());
        issueRepository.deleteById(id);
    }

    // TODO: use delete flag to soft delete ?
    @Transactional
    @Override
    public void deleteAllIssuesByUser(long userId) {
        List<Long> issueIds = issueRepository.findByUserId(userId)
                .stream()
                .map(Issue::getId)
                .toList();

        if (issueIds.isEmpty())
            return;

        commentService.deleteAllCommentsByIssueIds(issueIds);
        voteService.deleteAllVotesByIssueIds(issueIds);
        issueRepository.deleteAllById(issueIds);
    }

    // #endregion

    // ------------------------------
    // Helpers
    // ------------------------------

    private void validateReferences(long locationId, long categoryId) {

        EntityValidator.validateExists(locationRepository, locationId, "Location");
        EntityValidator.validateExists(categoryRepository, categoryId, "Category");
    }

    private EntityNotFoundException notFound(long id) {
        return new EntityNotFoundException("Issue not found with id: %d".formatted(id));
    }

}
