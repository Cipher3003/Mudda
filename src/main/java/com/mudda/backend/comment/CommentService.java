package com.mudda.backend.comment;

import com.mudda.backend.comment.dto.CommentResponse;
import com.mudda.backend.comment.dto.CommentCreatedResponse;
import com.mudda.backend.comment.dto.CommentUpdateResponse;
import com.mudda.backend.comment.dto.CreateCommentRequest;
import com.mudda.backend.comment.event.*;
import com.mudda.backend.issue.IssueRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Slf4j
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeService commentLikeService;
    private final IssueRepository issueRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public CommentService(
            CommentRepository commentRepository,
            CommentLikeService commentLikeService,
            IssueRepository issueRepository,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        this.commentRepository = commentRepository;
        this.commentLikeService = commentLikeService;
        this.issueRepository = issueRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    // region Queries (Read Operations)

    public Page<CommentResponse> findCommentsWithLikes(long issueId, Pageable pageable, Long userId) {
        if (userId == null)
            return commentRepository.findCommentFeed(issueId, pageable).map(CommentMapper::toCommentResponseFromProj);
        else
            return commentRepository.findCommentFeed(userId, issueId, pageable)
                    .map(p -> CommentMapper.toCommentResponseFromProj(p, userId));
    }

    public Optional<CommentResponse> findById(long commentId, Long userId) {
        if (userId == null)
            return commentRepository.findCommentById(commentId).map(CommentMapper::toCommentResponseFromProj);
        else
            return commentRepository.findCommentById(userId, commentId)
                    .map(p -> CommentMapper.toCommentResponseFromProj(p, userId));
    }

    public Page<CommentResponse> findAllReplies(long parentId, Pageable pageable, Long userId) {
        if (userId == null)
            return commentRepository.findReplyFeed(parentId, pageable).map(CommentMapper::toCommentResponseFromProj);
        else
            return commentRepository.findReplyFeed(userId, parentId, pageable)
                    .map(p -> CommentMapper.toCommentResponseFromProj(p, userId));
    }

    // endregion

    // region Commands (Write Operations)

    @Transactional
    public CommentCreatedResponse createComment(Long userId, CreateCommentRequest request) {

        Comment comment = request.parentId() == null
                ? createTopLevelComment(userId, request)
                : createReply(userId, request);

        applicationEventPublisher.publishEvent(new CommentCreatedEvent(request.issueId()));
        log.debug("Published CommentCreatedEvent for issue: {} and comment: {}",
                comment.getIssueId(), comment.getId());

        return CommentMapper.toCommentCreated(comment);
    }

    private Comment createTopLevelComment(long userId, CreateCommentRequest request) {
        if (!issueRepository.existsById(request.issueId()))
            throw new EntityNotFoundException("Issue not found with id: %s".formatted(request.issueId()));

        Comment comment = CommentMapper.toComment(request, userId);
        Comment saved = commentRepository.save(comment);
        log.info("Created Top Level Comment with id {} by user {} on issue {}",
                saved.getId(), userId, request.issueId());

        return saved;
    }

    public Comment createReply(long userId, CreateCommentRequest request) {
        Comment parent = commentRepository.findById(request.parentId()).orElseThrow(
                () -> new EntityNotFoundException("Comment with id: %d not found. Failed to create reply"
                        .formatted(request.parentId()))
        );

        Comment comment = CommentMapper.toReply(request, parent.getIssueId(), userId, request.parentId());
        Comment saved = commentRepository.save(comment);
        log.info("Created Reply Comment with id {} by user {} on issue {}",
                saved.getId(), userId, parent.getId());

        commentRepository.incrementReplyCount(parent.getId());
        log.debug("Incremented Reply Count for comment: {} with reply: {}",
                parent.getId(), saved.getId());

        return saved;
    }

    @Deprecated
    @Transactional
    public List<Long> createComments(
            List<Long> userIds,
            List<CreateCommentRequest> createCommentRequests
    ) {
        return commentRepository.saveAll(IntStream
                        .range(0, userIds.size())
                        .mapToObj(index -> CommentMapper.toComment(
                                createCommentRequests.get(index),
                                userIds.get(index)
                        ))
                        .toList())
                .stream()
                .map(Comment::getId)
                .toList();
    }

    //    TODO: improve the readability of create replies
    @Deprecated
    @Transactional
    public void createReplies(
            List<Long> parentIds, List<Long> userIds,
            List<Long> issueIds,
            List<CreateCommentRequest> createCommentRequests
    ) {
        commentRepository.saveAll(IntStream.
                range(0, parentIds.size())
                .mapToObj(index -> CommentMapper.toReply(
                        createCommentRequests.get(index),
                        issueIds.get(index),
                        userIds.get(index),
                        parentIds.get(index)
                ))
                .toList());
    }

    @Transactional
    public void saveComments(List<Comment> comments) {
        commentRepository.saveAll(comments);
    }

    @Transactional
    public CommentUpdateResponse updateComment(long id, String text) {
        if (text == null || text.isBlank())
            throw new IllegalArgumentException("Comment text cannot be empty.");

        Comment comment = commentRepository.findById(id).orElseThrow(() -> notFound(id));

        comment.updateDetails(text);
        Comment saved = commentRepository.save(comment);
        log.info("Updated comment with id {}", saved.getId());

        return CommentMapper.toCommentUpdated(saved);
    }

    @Transactional
    public void deleteComment(long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> notFound(id));

        if (comment.getParentId() == null) deleteTopLevelComment(comment);
        else deleteReply(comment);
    }

    private void deleteTopLevelComment(Comment comment) {
        Long id = comment.getId();
        commentLikeService.deleteAllFromTopLevelComment(id);
        log.debug("Deleted All likes from Top Level Comment with id {} and nested replies", id);

        long replyCount = commentRepository.countByParentId(id);
        commentRepository.deleteAllFromTopLevelComment(id);
        log.info("Deleted All comments from Top Level Comment with id {} and replies count: {}", id, replyCount);

        applicationEventPublisher.publishEvent(new TopLevelCommentRemovedEvent(
                comment.getIssueId(), 1 + replyCount
        ));
        log.debug("Published TopLevelCommentRemovedEvent for issue: {} and comment: {}", comment.getIssueId(), id);
    }

    private void deleteReply(Comment reply) {
        Long id = reply.getId();
        commentLikeService.deleteByCommentId(id);
        commentRepository.deleteById(id);

        commentRepository.decrementReplyCount(reply.getParentId());

        applicationEventPublisher.publishEvent(new CommentRemovedEvent(reply.getIssueId()));
        log.debug("Published CommentRemovedEvent for issue: {} and comment: {}", reply.getIssueId(), id);
    }

    @Transactional
    public void deleteAllCommentsByUserId(long userId) {

//        Parent Comments
        List<Long> commentIds = commentRepository.findByUserId(userId)
                .stream()
                .map(Comment::getId)
                .toList();

        if (commentIds.isEmpty()) return;

        // Fetch all replies
        List<Long> replyIds = commentRepository.findByParentIdIn(commentIds)
                .stream()
                .map(Comment::getId)
                .toList();

        List<Long> allIds = new ArrayList<>(commentIds);
        allIds.addAll(replyIds);

        log.trace("Deleting all likes on comment by user {}", userId);
        commentLikeService.deleteAllByCommentId(allIds);
        commentRepository.deleteAllById(allIds);
        log.info("Deleted all comments by user id {}", userId);
    }

    @Transactional
    public void deleteAllCommentsByIssueId(long issueId) {
//        Fetches all comments and their replies since comment and reply both refer to issue individually
        List<Long> commentIds = commentRepository.findByIssueId(issueId)
                .stream()
                .map(Comment::getId)
                .toList();

        if (commentIds.isEmpty()) return;

        log.trace("Deleting all likes on the comments under the issue with id {}", issueId);
        commentLikeService.deleteAllByCommentId(commentIds);
        commentRepository.deleteAllById(commentIds);
        log.info("Deleted all comments under the issue with id {}", issueId);
    }

    //    TODO: maybe refactor and combine above method and below method common functionality ?
    @Transactional
    public void deleteAllCommentsByIssueIds(List<Long> issueIds) {
//        Fetches all comments and their replies since comment and reply both refer to issue individually
        List<Long> commentIds = commentRepository.findByIssueIdIn(issueIds)
                .stream()
                .map(Comment::getId)
                .toList();

        if (commentIds.isEmpty()) return;

        log.trace("Deleting all likes on comments and replies under {} issues", issueIds.size());
        commentLikeService.deleteAllByCommentId(commentIds);
        commentRepository.deleteAllById(commentIds);
        log.info("Delete comments and replies under {} issues", issueIds.size());
    }

    // endregion

    // region Listeners

    @EventListener(CommentLikeCreatedEvent.class)
    public void incrementCommentLikeCount(CommentLikeCreatedEvent event) {
        commentRepository.incrementLikeCount(event.id());
    }

    @EventListener(CommentLikeRemovedEvent.class)
    public void decrementCommentLikeCount(CommentLikeRemovedEvent event) {
        commentRepository.decrementLikeCount(event.id());
    }

    // endregion

    //region Helpers

    private EntityNotFoundException notFound(long id) {
        return new EntityNotFoundException("Comment not found with id: %d".formatted(id));
    }

    //endregion

}
