package com.mudda.backend.comment;

import com.mudda.backend.issue.IssueRepository;
import com.mudda.backend.utils.EntityValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeService commentLikeService;
    private final CommentLikeRepository commentLikeRepository;
    private final IssueRepository issueRepository;

    public CommentServiceImpl(CommentRepository commentRepository,
                              CommentLikeService commentLikeService,
                              CommentLikeRepository commentLikeRepository,
                              IssueRepository issueRepository
    ) {
        this.commentRepository = commentRepository;
        this.commentLikeService = commentLikeService;
        this.commentLikeRepository = commentLikeRepository;
        this.issueRepository = issueRepository;
    }

    // region Queries (Read Operations)

    @Override
    public Page<CommentDetailResponse> findCommentsWithLikes(long issueId, Pageable pageable, Long userId) {
        Page<Comment> commentPage = commentRepository.findByIssueIdAndParentIdIsNull(issueId, pageable);

        List<Long> ids = commentPage.getContent()
                .stream()
                .map(Comment::getCommentId)
                .toList();

        if (userId == null)
            return commentPage.map(comment ->
                    getCommentResponseFromComment(comment, false,
                            false, false, false));

        Set<Long> likedIds = commentLikeRepository.findByUserIdAndCommentIdIn(userId, ids)
                .stream()
                .map(CommentLike::getCommentId)
                .collect(Collectors.toSet()); // Uses Set to keep lookup fast for mapping in below function

        return commentPage.map(comment -> getCommentResponseFromComment(
                comment,
                likedIds.contains(comment.getCommentId()),
                true,
                comment.getUserId().equals(userId),
                comment.getUserId().equals(userId)
        ));
    }

    @Override
    public Optional<CommentDetailResponse> findById(long commentId, Long userId) {
        boolean hasUserLiked = commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);

        return commentRepository
                .findById(commentId)
                .map(comment -> getCommentResponseFromComment(
                        comment,
                        hasUserLiked,
                        true,
                        comment.getUserId().equals(userId),
                        comment.getUserId().equals(userId)
                ));
    }

    @Override
    public Page<ReplyResponse> findAllReplies(long parentId, Pageable pageable, Long userId) {
        Page<Comment> replyPage = commentRepository.findByParentId(parentId, pageable);

        List<Long> ids = replyPage.getContent()
                .stream()
                .map(Comment::getCommentId)
                .toList();

        if (userId == null)
            return replyPage.map(comment -> getReplyResponseFromComment(
                    comment, false, false, false, false
            ));

        Set<Long> likedIds = commentLikeRepository.findByUserIdAndCommentIdIn(userId, ids)
                .stream()
                .map(CommentLike::getCommentId)
                .collect(Collectors.toSet()); // Uses Set to keep lookup fast for mapping in below function

        return replyPage.map(comment -> getReplyResponseFromComment(
                comment,
                likedIds.contains(comment.getCommentId()),
                true,
                comment.getUserId().equals(userId),
                comment.getUserId().equals(userId)
        ));
    }

    private CommentDetailResponse getCommentResponseFromComment(Comment comment,
                                                                boolean hasUserLiked,
                                                                boolean canUserLike,
                                                                boolean canUserUpdate,
                                                                boolean canUserDelete
    ) {
        long likes = commentLikeService.countByCommentId(comment.getCommentId());
        long replies = commentRepository.countByParentId(comment.getCommentId());

        return CommentMapper.toCommentResponse(comment, likes, replies, hasUserLiked,
                canUserLike, canUserUpdate, canUserDelete);
    }

    private ReplyResponse getReplyResponseFromComment(Comment comment,
                                                      boolean hasUserLiked,
                                                      boolean canUserLike,
                                                      boolean canUserUpdate,
                                                      boolean canUserDelete
    ) {
        long likes = commentLikeService.countByCommentId(comment.getCommentId());

        return CommentMapper.toReplyResponse(comment, likes, hasUserLiked,
                canUserLike, canUserUpdate, canUserDelete);
    }

    // endregion

    // region Commands (Write Operations)

    @Transactional
    @Override
    public CommentResponse createComment(long issueId, Long userId, CreateCommentRequest createCommentRequest) {
        validateCommentReferences(issueId);

//        TODO: change the exception to custom ?
        if (userId == null)
            throw new IllegalArgumentException("UserId not correct, Login with proper credentials");

        Comment comment = CommentMapper.toComment(createCommentRequest, issueId, userId);
        Comment saved = commentRepository.save(comment);
        log.info("Created comment with id {} by user {} on issue {}", saved.getCommentId(), userId, issueId);
        return CommentMapper.toCommentResponse(saved);
    }

    @Transactional
    @Override
    public List<Long> createComments(List<Long> issueIds, List<Long> userIds,
                                     List<CreateCommentRequest> createCommentRequests
    ) {
        return commentRepository
                .saveAll(IntStream
                        .range(0, issueIds.size())
                        .mapToObj(index ->
                                CommentMapper.toComment(
                                        createCommentRequests.get(index),
                                        issueIds.get(index),
                                        userIds.get(index)
                                ))
                        .toList())
                .stream()
                .map(Comment::getCommentId)
                .toList();
    }

    @Transactional
    @Override
    public CommentResponse createReply(long parentId, Long userId, CreateCommentRequest createCommentRequest) {

//        TODO: change the exception to custom ?
        if (userId == null)
            throw new IllegalArgumentException("UserId not correct, Login with proper credentials");

        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> notFound(parentId));

        Comment reply = CommentMapper.toReply(createCommentRequest, parent.getIssueId(), userId, parentId);
        Comment saved = commentRepository.save(reply);
        log.info("Created reply with id {} under comment {} by user {}", saved.getCommentId(), parentId, userId);
        return CommentMapper.toCommentResponse(saved);
    }

    //    TODO: improve the readability of create replies
    @Transactional
    @Override
    public List<Long> createReplies(List<Long> parentIds, List<Long> userIds,
                                    List<Long> issueIds,
                                    List<CreateCommentRequest> createCommentRequests
    ) {
        return commentRepository
                .saveAll(IntStream.range(0, parentIds.size())
                        .mapToObj(index ->
                                CommentMapper.toReply(
                                        createCommentRequests.get(index),
                                        issueIds.get(index),
                                        userIds.get(index),
                                        parentIds.get(index)))
                        .toList())
                .stream()
                .map(Comment::getCommentId)
                .toList();
    }

    @Transactional
    @Override
    public void saveComments(List<Comment> comments) {
        commentRepository.saveAll(comments);
    }

    @Transactional
    @Override
    public CommentResponse updateComment(long id, String text) {
        if (text == null || text.isBlank())
            throw new IllegalArgumentException("Comment text cannot be empty.");

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> notFound(id));

        comment.updateDetails(text);
        Comment saved = commentRepository.save(comment);
        log.info("Updated comment with id {}", saved.getCommentId());
        return CommentMapper.toCommentResponse(saved);
    }

    @Transactional
    @Override
    public void deleteComment(long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> notFound(id));

        if (comment.getParentId() == null) {
            List<Long> replyIds = commentRepository.findByParentId(comment.getCommentId())
                    .stream()
                    .map(Comment::getCommentId)
                    .toList();

            if (!replyIds.isEmpty()) {
                log.trace("Deleting all replies and reply likes under comment {}", id);
                commentLikeService.deleteAllByCommentId(replyIds);
                commentRepository.deleteByParentId(comment.getCommentId());
            }
        }

        log.trace("Deleting all likes on comment with id {}", id);
        commentLikeService.deleteByCommentId(comment.getCommentId());
        commentRepository.deleteById(id);
        log.info("Deleted comment with id {}", id);
    }

    @Transactional
    @Override
    public void deleteAllCommentsByUserId(long userId) {
//        Parent Comments
        List<Long> commentIds = commentRepository.findByUserId(userId)
                .stream()
                .map(Comment::getCommentId)
                .toList();

        if (commentIds.isEmpty()) return;

        // Fetch all replies
        List<Long> replyIds = commentRepository.findByParentIdIn(commentIds)
                .stream()
                .map(Comment::getCommentId)
                .toList();

        List<Long> allIds = new ArrayList<>(commentIds);
        allIds.addAll(replyIds);

        log.trace("Deleting all likes on comment by user {}", userId);
        commentLikeService.deleteAllByCommentId(allIds);
        commentRepository.deleteAllById(allIds);
        log.info("Deleted all comments by user id {}", userId);
    }

    @Transactional
    @Override
    public void deleteAllCommentsByIssueId(long issueId) {
//        Fetches all comments and their replies since comment and reply both refer to issue individually
        List<Long> commentIds = commentRepository.findByIssueId(issueId)
                .stream()
                .map(Comment::getCommentId)
                .toList();

        if (commentIds.isEmpty()) return;

        log.trace("Deleting all likes on the comments under the issue with id {}", issueId);
        commentLikeService.deleteAllByCommentId(commentIds);
        commentRepository.deleteAllById(commentIds);
        log.info("Deleted all comments under the issue with id {}", issueId);
    }

    //    TODO: maybe refactor and combine above method and below method common functionality ?
    @Transactional
    @Override
    public void deleteAllCommentsByIssueIds(List<Long> issueIds) {
//        Fetches all comments and their replies since comment and reply both refer to issue individually
        List<Long> commentIds = commentRepository.findByIssueIdIn(issueIds)
                .stream()
                .map(Comment::getCommentId)
                .toList();

        if (commentIds.isEmpty()) return;

        log.trace("Deleting all likes on comments and replies under {} issues", issueIds.size());
        commentLikeService.deleteAllByCommentId(commentIds);
        commentRepository.deleteAllById(commentIds);
        log.info("Delete comments and replies under {} issues", issueIds.size());
    }

    @Transactional
    @Override
    public CommentLikeResponse likeComment(long commentId, Long userId) {

//        TODO: change the exception to custom ?
        if (userId == null)
            throw new IllegalArgumentException("UserId not correct, Login with proper credentials");

        return commentLikeService.userLikesOnComment(commentId, userId);
    }

    @Transactional
    @Override
    public CommentLikeResponse deleteLikeComment(long commentId, Long userId) {

//        TODO: change the exception to custom ?
        if (userId == null)
            throw new IllegalArgumentException("UserId not correct, Login with proper credentials");

        return commentLikeService.userRemovesLikeFromComment(commentId, userId);
    }

    // endregion

    //region Helpers

    private void validateCommentReferences(long issueId) {
        EntityValidator.validateExists(issueRepository, issueId, "Issue");
    }

    private EntityNotFoundException notFound(long id) {
        return new EntityNotFoundException("Comment not found with id: %d".formatted(id));
    }

    //endregion

}
