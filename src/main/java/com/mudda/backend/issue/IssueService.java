package com.mudda.backend.issue;

import com.mudda.backend.comment.CommentService;
import com.mudda.backend.comment.event.CommentCreatedEvent;
import com.mudda.backend.comment.event.CommentRemovedEvent;
import com.mudda.backend.issue.dto.*;
import com.mudda.backend.media.Media;
import com.mudda.backend.media.MediaOwner;
import com.mudda.backend.media.MediaRepository;
import com.mudda.backend.media.MediaService;
import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.UserRepository;
import com.mudda.backend.vote.*;
import io.awspring.cloud.sqs.operations.SendResult;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final CommentService commentService;
    private final VoteRepository voteRepository;
    private final VoteService voteService;
    private final MediaService mediaService;
    private final MediaRepository mediaRepository;
    private final SqsTemplate sqsTemplate;

    private static final String QUEUE_NAME = "mudda-hate-speech-queue";
    public static final String CDN_URL = "cdn";

    public IssueService(
            IssueRepository issueRepository, CommentService commentService,
            VoteRepository voteRepository, VoteService voteService,
            UserRepository userRepository, SqsTemplate sqsTemplate,
            MediaService mediaService, MediaRepository mediaRepository
    ) {
        this.issueRepository = issueRepository;
        this.commentService = commentService;
        this.voteRepository = voteRepository;
        this.voteService = voteService;
        this.userRepository = userRepository;
        this.sqsTemplate = sqsTemplate;
        this.mediaService = mediaService;
        this.mediaRepository = mediaRepository;
    }

    // region Queries (Read Operations)

    public Page<IssueResponse> findAllIssues(IssueFilterRequest filterRequest, Pageable pageable, Long userId) {
        log.debug("Finding all issues by filter request {}", filterRequest);

        IssueCategory category = IssueCategory.fromCode(filterRequest.category());
        IssueStatus status = IssueStatus.fromCode(filterRequest.status());

        Specification<Issue> specification = IssueSpecifications
                .containsText(filterRequest.search())
                .and(IssueSpecifications.hasStatus(status))
                .and(IssueSpecifications.hasUserId(filterRequest.userId()))
                .and(IssueSpecifications.hasCategory(category))
                .and(IssueSpecifications.hasCity(filterRequest.city()))
                .and(IssueSpecifications.hasState(filterRequest.state()))
                .and(IssueSpecifications.isUrgent(filterRequest.urgency()))
                .and(IssueSpecifications.isDeleted(false))
                .and(IssueSpecifications.severityBetween(filterRequest.minSeverity(), filterRequest.maxSeverity()))
                .and(IssueSpecifications.createdAfter(filterRequest.createdAfter()))
                .and(IssueSpecifications.createdBefore(filterRequest.createdBefore()));

        Page<Issue> issuePage = issueRepository.findAll(specification, pageable);

        List<Long> issueIds = issuePage.getContent().stream()
                .map(Issue::getId)
                .toList();

        Map<Long, List<String>> mediaUrlMap = mediaRepository
                .findByOwnerIdInAndOwnerType(issueIds, MediaOwner.ISSUE)
                .stream()
                .collect(Collectors.groupingBy(
                        Media::getOwnerId,
                        Collectors.mapping(
                                media -> CDN_URL.concat(media.getMediaKey()),
                                Collectors.toList()
                        )
                ));

        Set<Long> authorIds = issuePage.getContent().stream()
                .map(Issue::getUserId)
                .collect(Collectors.toSet());

        Map<Long, MuddaUser> usersMap = userRepository.findAllById(authorIds)
                .stream()
                .collect(Collectors.toMap(MuddaUser::getUserId, user -> user));

        boolean isAuthenticated = userId != null;

        // TODO: maybe use Vote entity's own check vote casted by user to filter this
        // set even more
        Set<Long> issuesVotedByUser = isAuthenticated
                ? voteRepository.findByUserIdAndIssueIdIn(userId, issueIds)
                .stream()
                .map(Vote::getIssueId)
                .collect(Collectors.toSet())
                : Set.of();

        return issuePage.map(issue -> {

            MuddaUser muddaUser = usersMap.getOrDefault(issue.getUserId(), null);
            if (muddaUser == null)
                throw new IllegalStateException("User not found for issue: " + issue.getId());

            // TODO: maybe continue and ignore bad data and cleanup later

            boolean hasUserVoted = issuesVotedByUser.contains(issue.getId());

            // TODO: maybe prevent self voting
            return IssueMapper.toResponse(
                    issue, mediaUrlMap.get(issue.getId()), muddaUser, hasUserVoted,
                    isAuthenticated, isAuthenticated, isAuthenticated, isAuthenticated
            );
        });
    }

    public Page<IssueSummaryResponse> findAllIssuesByAuthor(Pageable pageable, Long userId) {

        Page<Issue> issuePage = issueRepository.findByUserId(userId, pageable);

        List<Long> issueIds = issuePage.getContent()
                .stream()
                .map(Issue::getId)
                .toList();

        Map<Long, List<String>> mediaUrlMap = mediaRepository
                .findByOwnerIdInAndOwnerType(issueIds, MediaOwner.ISSUE)
                .stream()
                .collect(Collectors.groupingBy(
                        Media::getOwnerId,
                        Collectors.mapping(
                                media -> CDN_URL.concat(media.getMediaKey()),
                                Collectors.toList()
                        )
                ));

        Set<Long> authorIds = issuePage.getContent()
                .stream()
                .map(Issue::getUserId)
                .collect(Collectors.toSet());

        Map<Long, MuddaUser> usersMap = userRepository.findAllById(authorIds)
                .stream()
                .collect(Collectors.toMap(MuddaUser::getUserId, user -> user));

        // TODO: maybe use Vote entity's own check vote casted by user to filter this
        // set even more
        Set<Long> issuesVotedByUser = voteRepository.findByUserIdAndIssueIdIn(userId, issueIds)
                .stream()
                .map(Vote::getIssueId)
                .collect(Collectors.toSet());

        return issuePage.map(issue -> {

            MuddaUser muddaUser = usersMap.getOrDefault(issue.getUserId(), null);
            if (muddaUser == null)
                throw new IllegalStateException("User not found for issue: " + issue.getId());
            // TODO: maybe continue and ignore bad data and cleanup later

            boolean hasUserVoted = issuesVotedByUser.contains(issue.getId());

            // TODO: maybe prevent self voting
            return IssueMapper.toSummary(issue, mediaUrlMap.get(issue.getId()), muddaUser, hasUserVoted, true);
            // canVote true since this method is called by protected endpoint
        });
    }

    public Optional<IssueResponse> findById(long id, Long userId) {

        Issue issue = issueRepository.findById(id).orElse(null);
        if (issue == null)
            return Optional.empty();

        MuddaUser author = userRepository.findById(issue.getUserId()).orElse(null);
        if (author == null)
            return Optional.empty();

        List<String> mediaUrls = mediaRepository.findByOwnerIdAndOwnerType(issue.getId(), MediaOwner.ISSUE)
                .stream()
                .map(media -> CDN_URL.concat(media.getMediaKey()))
                .toList();

        boolean isAuthenticated = userId != null;
        boolean isOwner = isAuthenticated && issue.getUserId().equals(userId);

        boolean hasUserVoted = isAuthenticated
                && voteRepository.existsByIssueIdAndUserId(issue.getId(), userId);

        return Optional.of(IssueMapper.toResponse(
                issue, mediaUrls, author, hasUserVoted,
                isAuthenticated, // canVote
                isAuthenticated, // canComment
                isOwner, // canEdit
                isOwner // canDelete
        ));
    }

    // Reference:
    // https://sud-gajula.medium.com/handling-geo-spatial-data-in-postgres-with-h3-05838fb77fd8
    public IssueClusterResponse findAllIssueClusters(IssueClusterRequest clusterRequest) {
        // TODO: move to db
        log.debug("Finding all issue clusters by request {}", clusterRequest);

        double centerLat = (clusterRequest.minLatitude() + clusterRequest.maxLatitude()) / 2;
        double cellSize = getCellSize(clusterRequest.zoomLevel(), centerLat);

        List<IssueClusterQueryResult> issueClusters = issueRepository.getIssueClusters(
                clusterRequest.minLatitude(), clusterRequest.maxLatitude(), clusterRequest.minLongitude(),
                clusterRequest.maxLongitude(), cellSize);

        if (issueClusters == null || issueClusters.isEmpty())
            return new IssueClusterResponse(List.of());

        Map<String, List<IssueClusterQueryResult>> grouped = issueClusters.stream()
                .collect(Collectors.groupingBy(cluster -> cluster.cellX() + "_" + cluster.cellY()));

        List<IssueClusterDTO> clusters = new ArrayList<>(grouped.size());

        for (List<IssueClusterQueryResult> group : grouped.values()) {
            Map<String, Long> categoryCounts = group.stream().collect(Collectors.toMap(
                    IssueClusterQueryResult::category, IssueClusterQueryResult::count));

            String topCategory = categoryCounts.entrySet().stream().max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey).orElse("unknown");

            IssueClusterQueryResult firstCluster = group.get(0);
            clusters.add(new IssueClusterDTO(
                    firstCluster.centerLatitude(), firstCluster.centerLongitude(), topCategory,
                    categoryCounts
            ));
        }

        return new IssueClusterResponse(clusters);
    }

    public Page<IssueDashboardResponse> findAllIssuesDashboard(Pageable pageable) {
        // TODO: refactor this method whole dashboard endpoint and service
        Specification<Issue> specification = IssueSpecifications.isDeleted(false);

        Page<Issue> issuePage = issueRepository.findAll(specification, pageable);

        List<Long> issueIds = issuePage.getContent()
                .stream()
                .map(Issue::getId)
                .toList();

        Map<Long, List<String>> mediaUrlMap = mediaRepository
                .findByOwnerIdInAndOwnerType(issueIds, MediaOwner.ISSUE)
                .stream()
                .collect(Collectors.groupingBy(
                        Media::getOwnerId,
                        Collectors.mapping(
                                media -> CDN_URL.concat(media.getMediaKey()),
                                Collectors.toList()
                        )
                ));

        Set<Long> authorIds = issuePage.getContent()
                .stream()
                .map(Issue::getUserId)
                .collect(Collectors.toSet());

        Map<Long, MuddaUser> usersMap = userRepository.findAllById(authorIds)
                .stream()
                .collect(Collectors.toMap(MuddaUser::getUserId, user -> user));

        Map<Long, Long> voteCountMap = voteRepository.countByIssueIdIn(issueIds)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0], // issueId
                        row -> (Long) row[1] // count
                ));

        return issuePage.map(issue -> {

            MuddaUser muddaUser = usersMap.getOrDefault(issue.getUserId(), null);
            if (muddaUser == null)
                throw new IllegalStateException("User not found for issue: " + issue.getId());

            long voteCount = voteCountMap.getOrDefault(issue.getId(), 0L);

            return IssueMapper.forDashboard(issue, mediaUrlMap.get(issue.getId()), muddaUser, voteCount);
        });
    }

    // endregion

    // region Commands (Write Operations)

    @Transactional
    public IssueResponse createIssue(Long userId, CreateIssueRequest issueRequest) {
        MuddaUser muddaUser = userRepository.findById(userId).orElse(null);
        if (muddaUser == null) throw new IllegalArgumentException("User not found for creating Issue");

        Issue issue = IssueMapper.toIssue(userId, issueRequest);

        Issue saved = issueRepository.save(issue);
        log.info("Created Issue with id {} by user with id {}", saved.getId(), userId);

        mediaService.linkToIssue(saved.getId(), issueRequest.mediaUrls());

        List<String> mediaUrls = issueRequest.mediaUrls().stream().map(CDN_URL::concat).toList();

        IssueCreatedEvent issueCreatedEvent = new IssueCreatedEvent(
                saved.getId(), userId, false, saved.getTitle() + saved.getDescription()
        );

        SendResult<IssueCreatedEvent> result = sqsTemplate.send(QUEUE_NAME, issueCreatedEvent);
        log.trace("Event: {} result: {} ", IssueCreatedEvent.class.getSimpleName(), result);

        log.info("Sent event: {} to queue: {}", IssueCreatedEvent.class.getSimpleName(), QUEUE_NAME);

        return IssueMapper.toResponse(
                saved, mediaUrls, muddaUser,
                false, true, true,
                true, true
        );
    }

    @Transactional
    public List<Long> createIssues(List<Long> userIds, List<CreateIssueRequest> issueRequests) {
        List<Issue> issues = issueRepository.saveAll(
                IntStream
                        .range(0, userIds.size())
                        .mapToObj(index -> IssueMapper.toIssue(userIds.get(index), issueRequests.get(index)))
                        .toList());

        log.info("Created {} Issues", issues.size());
        return issues.stream().map(Issue::getId).toList();
    }

    @Transactional
    public void saveIssues(List<Issue> issues) {
        issueRepository.saveAll(issues);
        log.info("Saved {} Issues", issues.size());
    }

    @Transactional
    public IssueUpdateResponse updateIssue(long id, Long userId, UpdateIssueRequest issueRequest) {

        // TODO: change the exception to custom ?
        if (userId == null)
            throw new IllegalArgumentException("UserId not correct, Login with proper credentials");

        Issue existing = issueRepository.findById(id).orElseThrow(() -> notFound(id));

        boolean isOwner = existing.getUserId().equals(userId);
        if (!isOwner)
            throw new IllegalStateException("Only author can update their posted issue");

        IssueStatus status = IssueStatus.fromCode(issueRequest.status());
        existing.updateDetails(issueRequest.title(), issueRequest.description(), status);
        Issue updated = issueRepository.save(existing);
        log.info("Updated Issue with id {} by user with id {}", updated.getId(), userId);

        IssueCreatedEvent issueCreatedEvent = new IssueCreatedEvent(
                updated.getId(), userId, true, updated.getTitle() + updated.getDescription());
        SendResult<IssueCreatedEvent> result = sqsTemplate.send(QUEUE_NAME, issueCreatedEvent);
        log.trace("Update Event: {} result: {} ", IssueCreatedEvent.class.getSimpleName(), result);
        log.info("Sent update event: {} to queue: {}", IssueCreatedEvent.class.getSimpleName(), QUEUE_NAME);

        return IssueMapper.toUpdateResponse(updated);
    }

    // TODO: use delete flag to soft delete ?
    // TODO: delete media rows
    @Transactional
    public void deleteIssue(long id, Long userId) {

        // TODO: change the exception to custom ?
        if (userId == null)
            throw new IllegalArgumentException("UserId not correct, Login with proper credentials");

        Issue issue = issueRepository.findById(id).orElseThrow(() -> notFound(id));

        boolean isOwner = issue.getUserId().equals(userId);
        if (!isOwner)
            throw new IllegalStateException("Only author can delete their posted issue");

        log.trace("Deleting all comments under issue {}", issue.getId());
        commentService.deleteAllCommentsByIssueId(issue.getId());

        log.trace("Deleting all votes under issue {}", issue.getId());
        voteService.deleteAllVotesByIssueId(issue.getId());

        issueRepository.deleteById(id);
        log.info("Deleted Issue with id {} by user with id {}", issue.getId(), userId);
    }

    // TODO: use delete flag to soft delete ?
    @Transactional
    public void deleteAllIssuesByUser(long userId) {
        List<Long> issueIds = issueRepository.findByUserId(userId).stream().map(Issue::getId).toList();

        if (issueIds.isEmpty())
            return;

        log.trace("Deleting all comments under multiple issues");
        commentService.deleteAllCommentsByIssueIds(issueIds);

        log.trace("Deleting all votes under multiple issues");
        voteService.deleteAllVotesByIssueIds(issueIds);

        issueRepository.deleteAllById(issueIds);
        log.info("Deleted {} issues", issueIds.size());
    }

    // endregion

    // region Listeners

    @EventListener(VoteCratedEvent.class)
    // Should work in sync and same transaction as caller as claimed by internet
    public void incrementVote(VoteCratedEvent event) {
        issueRepository.incrementVoteCount(event.issueId());
    }

    @EventListener(VoteRemovedEvent.class)
    public void decrementVote(VoteRemovedEvent event) {
        issueRepository.decrementVoteCount(event.issueId());
    }

    @EventListener(CommentCreatedEvent.class)
    public void incrementCommentCount(CommentCreatedEvent event) {
        issueRepository.incrementCommentCount(event.issueId());
    }

    @EventListener(CommentRemovedEvent.class)
    public void decrementCommentCount(CommentRemovedEvent event) {
        issueRepository.decrementCommentCount(event.issueId());
    }

    // endregion

    // region Helpers

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

    // endregion

}
