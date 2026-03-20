package com.mudda.backend.issue;

import com.mudda.backend.comment.CommentService;
import com.mudda.backend.comment.event.CommentCreatedEvent;
import com.mudda.backend.comment.event.CommentRemovedEvent;
import com.mudda.backend.comment.event.TopLevelCommentRemovedEvent;
import com.mudda.backend.issue.dto.*;
import com.mudda.backend.media.*;
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

import java.time.Instant;
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
    public static final String CDN_URL = "cdn"; // TODO: inject CDN url

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
                .and(IssueSpecifications.isNotDeleted())
                .and(IssueSpecifications.severityBetween(filterRequest.minSeverity(), filterRequest.maxSeverity()))
                .and(IssueSpecifications.createdAfter(filterRequest.createdAfter()))
                .and(IssueSpecifications.createdBefore(filterRequest.createdBefore()))
                .and(IssueSpecifications.fetchAuthor());

        Page<Issue> issuePage = issueRepository.findAll(specification, pageable);

        List<Long> issueIds = issuePage.getContent().stream()
                .map(Issue::getId)
                .toList();

        Map<Long, List<String>> mediaUrlMap = getIssueIdsMediaUrlsMap(issueIds);

        boolean isAuthenticated = userId != null;

        Set<Long> issuesVotedByUser = isAuthenticated
                ? new HashSet<>(voteRepository.findByUserIdAndIssueIdIn(userId, issueIds))
                : Set.of();

        return issuePage.map(issue -> IssueMapper.toResponse(
                issue,
                mediaUrlMap.get(issue.getId()),
                issue.getAuthor(),
                issuesVotedByUser.contains(issue.getId()),
                isAuthenticated, // NOTE: Allowed self voting
                isAuthenticated, isAuthenticated, isAuthenticated
        ));
    }

    public Page<IssueSummaryResponse> findAllIssuesByAuthor(Pageable pageable, MuddaUser user) {

        Page<Issue> issuePage = issueRepository.findByUserId(user.getUserId(), pageable);

        List<Long> issueIds = issuePage.getContent().stream()
                .map(Issue::getId)
                .toList();

        Map<Long, List<String>> mediaUrlMap = getIssueIdsMediaUrlsMap(issueIds);

        // NOTE: Allowed self voting
        Set<Long> issuesVotedByUser = new HashSet<>(
                voteRepository.findByUserIdAndIssueIdIn(user.getUserId(), issueIds)
        );

        return issuePage.map(issue -> IssueMapper.toSummary(
                issue,
                mediaUrlMap.get(issue.getId()),
                user,
                issuesVotedByUser.contains(issue.getId()),
                true
        ));
    }

    public IssueResponse findById(long id, Long userId) {

        boolean isAuthenticated = userId != null;

        IssueDetailProjection projection = isAuthenticated
                ? issueRepository.findIssueDetailProjectionById(id, userId).orElseThrow(() -> notFound(id))
                : issueRepository.findIssueDetailProjectionById(id).orElseThrow(() -> notFound(id));

        List<String> mediaUrls = mediaRepository
                .findMediaKeysByOwnerIdAndOwnerType(projection.getId(), MediaOwner.ISSUE).stream()
                .map(IssueService::toPublicUrl)
                .toList();

        boolean isAuthor = isAuthenticated && userId.equals(projection.getUserId());

        return IssueMapper.toResponse(
                projection, mediaUrls,
                isAuthenticated, // canVote
                isAuthenticated, // canComment
                isAuthor, // canEdit
                isAuthor // canDelete
        );
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
                .collect(Collectors.groupingBy(
                        cluster -> cluster.cellX() + "_" + cluster.cellY()
                ));

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
        // TODO: refactor this whole dashboard endpoint and service
        Specification<Issue> specification = IssueSpecifications
                .isNotDeleted()
                .and(IssueSpecifications.fetchAuthor());

        Page<Issue> issuePage = issueRepository.findAll(specification, pageable);
        Map<Long, List<String>> mediaUrlMap = getIssueIdsMediaUrlsMap(issuePage.getContent().stream().map(Issue::getId).toList());

        return issuePage.map(issue -> IssueMapper.forDashboard(
                issue,
                mediaUrlMap.get(issue.getId()),
                issue.getAuthor()
        ));
    }

    // endregion

    // region Commands (Write Operations)

    @Transactional
    public IssueResponse createIssue(Long userId, CreateIssueRequest issueRequest) {
        MuddaUser muddaUser = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User not found for creating Issue")
        );  // TODO: if JWT request comes can we rely on DB foreign key to validate user

        Issue issue = IssueMapper.toIssue(userId, issueRequest);

        Issue saved = issueRepository.save(issue);
        log.info("Created Issue with id {} by user with id {}", saved.getId(), userId);

        int links = mediaService.linkToIssue(saved.getId(), issueRequest.mediaUrls());
        if (links != issueRequest.mediaUrls().size()) {
            mediaService.removeImageFromOwner(saved.getId(), MediaOwner.ISSUE);
            log.warn("Media Linkage Failed for Issue id:{}", saved.getId());
            throw new IllegalStateException("""
                    Issue with id: %d has mismatch in media links, requested media size: %d, linked media size: %d"""
                    .formatted(saved.getId(), issueRequest.mediaUrls().size(), links));
        }

        List<String> mediaUrls = issueRequest.mediaUrls().stream()
                .map(IssueService::toPublicUrl)
                .toList();

        IssueCreatedEvent issueCreatedEvent = new IssueCreatedEvent(
                saved.getId(), userId, false, saved.getTitle() + saved.getDescription()
        );

        SendResult<IssueCreatedEvent> result = sqsTemplate.send(QUEUE_NAME, issueCreatedEvent);
        log.trace("Event: {} push result: {} ", IssueCreatedEvent.class.getSimpleName(), result);

        log.info("Sent event: {} to queue: {}", IssueCreatedEvent.class.getSimpleName(), QUEUE_NAME);

        return IssueMapper.toResponse(
                saved, mediaUrls, muddaUser,
                false, true, true,
                true, true
        );
    }

    @Deprecated
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
        // TODO: if JWT request comes validate user - fetch fresh from db
        // Maybe fetch author with issue and validate authorId, and permissions

        Issue existing = issueRepository.findById(id).orElseThrow(() -> notFound(id));

        boolean isOwner = existing.getUserId().equals(userId);
        if (!isOwner) throw new IllegalStateException("Only author can update their posted issue");
        // TODO: use custom exception - forbidden action

        IssueStatus status = IssueStatus.fromCode(issueRequest.status());
        // TODO: block user from changing status - only govt, creator or system allowed
        existing.updateDetails(issueRequest.title(), issueRequest.description(), status);
        Issue updated = issueRepository.save(existing);
        log.info("Updated Issue with id {} by user with id {}", updated.getId(), userId);

        // TODO: use updated event
        IssueCreatedEvent issueCreatedEvent = new IssueCreatedEvent(
                updated.getId(), userId, true, updated.getTitle() + updated.getDescription()
        );
        SendResult<IssueCreatedEvent> result = sqsTemplate.send(QUEUE_NAME, issueCreatedEvent);
        log.trace("Update Event: {} result: {} ", IssueCreatedEvent.class.getSimpleName(), result);
        log.info("Sent update event: {} to queue: {}", IssueCreatedEvent.class.getSimpleName(), QUEUE_NAME);

        return IssueMapper.toUpdateResponse(updated);
    }

    // SOFT delete content (issue, comment), hard delete interactions (likes, votes)
    // hide soft deleted content in feed - redact or mask deleted content
    @Transactional
    public void deleteIssue(long id, Long userId) {
        // TODO: if JWT request comes validate user - fetch fresh from db
        // Maybe fetch author with issue and validate authorId, and permissions

        Issue issue = issueRepository.findById(id).orElseThrow(() -> notFound(id));

        boolean isOwner = issue.getUserId().equals(userId);
        if (!isOwner) throw new IllegalStateException("Only author can delete their posted issue");

        log.trace("Deleting all votes under issue {}", issue.getId());
        voteService.deleteAllVotesByIssueId(issue.getId());

        issueRepository.softDeleteById(id, Instant.now());
        log.info("Soft Deleted Issue with id {} by user with id {}", issue.getId(), userId);
    }

    // TODO: use delete flag to soft delete ?
    @Transactional
    public void deleteAllIssuesByUser(long userId) {
        List<Long> issueIds = issueRepository.findByUserId(userId).stream().map(Issue::getId).toList();

        if (issueIds.isEmpty()) return;

        log.trace("Deleting all comments under multiple issues");
        commentService.deleteAllCommentsByIssueIds(issueIds);

        log.trace("Deleting all votes under multiple issues");
        voteService.deleteAllVotesByIssueIds(issueIds);

        issueRepository.deleteAllById(issueIds);
        log.info("Deleted {} issues", issueIds.size());
    }

    @Transactional
    public void softDelete(Long id) {
        issueRepository.softDeleteById(id, Instant.now());
    }

    @Transactional
    public void restoreIssue(Long id) {
        issueRepository.softDeleteById(id, null);
    }

    // endregion

    // region Listeners

    @EventListener(VoteCreatedEvent.class)
    // Should work in sync and same transaction as caller as claimed by internet
    public void incrementVote(VoteCreatedEvent event) {
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

    @EventListener(TopLevelCommentRemovedEvent.class)
    public void decrementCommentCounts(TopLevelCommentRemovedEvent event) {
        issueRepository.decrementCommentCount(event.issueId(), event.size());
    }

    // endregion

    // region Helpers

    private Map<Long, List<String>> getIssueIdsMediaUrlsMap(List<Long> issueIds) {
        return mediaRepository.findByOwnerIdInAndOwnerType(issueIds, MediaOwner.ISSUE).stream()
                .collect(Collectors.groupingBy(
                        MediaProjection::getOwnerId,
                        Collectors.mapping(
                                media -> toPublicUrl(media.getMediaKey()),
                                Collectors.toList()
                        )
                ));
    }

    private static String toPublicUrl(String media) {
        return "%s/%s".formatted(CDN_URL, media);
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

    // endregion

}
