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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    public Page<IssueSummaryResponse> findAllIssues(
            IssueFilterRequest filterRequest,
            Pageable pageable,
            Long userId) {

        List<Long> locationIds = locationRepository
                .findByCityAndState(filterRequest.city(), filterRequest.state())
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

        Map<Long, Long> voteCountMap = voteRepository
                .countByIssueIdIn(issueIds)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0], // issueId
                        row -> (Long) row[1] // count
                ));

        boolean isAuthenticated = userId != null;

        // TODO: maybe use Vote entity's own check vote casted by user to filter this
        // set even more
        Set<Long> issuesVotedByUser = isAuthenticated
                ? voteRepository.findByUserIdAndIssueIdIn(userId, issueIds).stream()
                .map(Vote::getIssueId).collect(Collectors.toSet())
                : Set.of();

        return issuePage.map(issue -> {
            long voteCount = voteCountMap.getOrDefault(issue.getId(), 0L);

            boolean hasUserVoted = issuesVotedByUser.contains(issue.getId());

            return IssueMapper.toSummary(
                    issue,
                    voteCount,
                    hasUserVoted,
                    isAuthenticated // canUserVote
            );
        });
    }

    @Override
    public Optional<IssueResponse> findById(long id, Long userId) {
        Issue issue = issueRepository.findById(id).orElse(null);
        if (issue == null)
            return Optional.empty();

        long voteCount = voteRepository.countByIssueId(issue.getId());

        Location location = locationRepository.findById(issue.getLocationId()).orElse(null);
        if (location == null)
            return Optional.empty();

        LocationDTO summary = LocationMapper.toSummary(location);

        return categoryRepository.findById(issue.getCategoryId())
                .map(category -> {

                    boolean isAuthenticated = userId != null;
                    boolean isOwner = isAuthenticated && issue.getUserId().equals(userId);
                    boolean hasUserVoted = isAuthenticated
                            && voteRepository.existsByIssueIdAndUserId(issue.getId(), userId);

                    return IssueMapper.toResponse(
                            issue,
                            summary,
                            category.getName(),
                            voteCount,
                            hasUserVoted,
                            isAuthenticated, // canUserVote
                            isAuthenticated, // canUserComment
                            isOwner, // canEdit
                            isOwner // canDelete
                    );
                });
    }

    // Reference:
    // https://sud-gajula.medium.com/handling-geo-spatial-data-in-postgres-with-h3-05838fb77fd8
    @Override
    public IssueClusterResponse findAllIssueClusters(IssueClusterRequest clusterRequest) {
        double centerLat = (clusterRequest.minLatitude() + clusterRequest.maxLatitude()) / 2;
        double cellSize = getCellSize(clusterRequest.zoomLevel(), centerLat);

        List<IssueClusterQueryResult> issueClusters = issueRepository.getIssueClusters(
                clusterRequest.minLatitude(), clusterRequest.maxLatitude(),
                clusterRequest.minLongitude(), clusterRequest.maxLongitude(),
                cellSize);

        if (issueClusters == null || issueClusters.isEmpty())
            return new IssueClusterResponse(List.of());

        Map<String, List<IssueClusterQueryResult>> grouped = issueClusters.stream()
                .collect(Collectors
                        .groupingBy(cluster -> cluster.cellX() + "_" + cluster.cellY()));

        List<IssueClusterDTO> clusters = new ArrayList<>(grouped.size());

        for (List<IssueClusterQueryResult> group : grouped.values()) {
            Map<String, Long> categoryCounts = group.stream()
                    .collect(Collectors
                            .toMap(
                                    IssueClusterQueryResult::category,
                                    IssueClusterQueryResult::count));

            String topCategory = categoryCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("unknown");

            IssueClusterQueryResult firstCluster = group.get(0);
            clusters.add(
                    new IssueClusterDTO(
                            firstCluster.centerLatitude(),
                            firstCluster.centerLongitude(),
                            topCategory,
                            categoryCounts));
        }

        return new IssueClusterResponse(clusters);
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
    public void saveIssues(List<Issue> issues) {
        issueRepository.saveAll(issues);
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

    private double getCellSize(int zoomLevel, double centerLat) {
        double meters;

        if (zoomLevel >= 17)
            meters = 10;
        else if (zoomLevel >= 15)
            meters = 50;
        else if (zoomLevel >= 13)
            meters = 200;
        else if (zoomLevel >= 10)
            meters = 1000;
        else
            meters = 5000;

        return metersToDegrees(meters, centerLat);
    }

    private double metersToDegrees(double meters, double latitude) {
        return meters / (111_320 * Math.cos(Math.toRadians(latitude)));
    }

}
