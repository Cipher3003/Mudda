package com.mudda.backend.comment;

import com.mudda.backend.issue.IssueRepository;
import com.mudda.backend.utils.EntityValidator;
import jakarta.persistence.EntityNotFoundException;
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

    // #region Queries (Read Operations)

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

    // #endregion

    // #region Commands (Write Operations)

    @Transactional
    @Override
    public CommentResponse createComment(long issueId, Long userId, CreateCommentRequest createCommentRequest) {
        validateCommentReferences(issueId);

//        TODO: change the exception to custom ?
        if (userId == null)
            throw new IllegalArgumentException("UserId not correct, Login with proper credentials");

        Comment comment = CommentMapper.toComment(createCommentRequest, issueId, userId);
        Comment saved = commentRepository.save(comment);
        return CommentMapper.toCommentResponse(saved);
    }

    @Transactional
    @Override
    public List<Long> createComments(List<Long> issueIds, List<Long> userIds,
                                     List<CreateCommentRequest> createCommentRequests
    ) {
        return commentRepository
                .saveAll(
                        IntStream
                                .range(0, issueIds.size())
                                .mapToObj(index ->
                                        CommentMapper.toComment(
                                                createCommentRequests.get(index),
                                                issueIds.get(index),
                                                userIds.get(index)
                                        )
                                )
                                .toList()
                )
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
        return CommentMapper.toCommentResponse(saved);
    }

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
                                        parentIds.get(index)
                                )
                        )
                        .toList()
                )
                .stream()
                .map(Comment::getCommentId)
                .toList();
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
                commentLikeService.deleteAllByCommentId(replyIds);
                commentRepository.deleteByParentId(comment.getCommentId());
            }
        }

        commentLikeService.deleteByCommentId(comment.getCommentId());
        commentRepository.deleteById(id);
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

        commentLikeService.deleteAllByCommentId(allIds);
        commentRepository.deleteAllById(allIds);
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

        commentLikeService.deleteAllByCommentId(commentIds);
        commentRepository.deleteAllById(commentIds);
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

        commentLikeService.deleteAllByCommentId(commentIds);
        commentRepository.deleteAllById(commentIds);
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

    // #endregion

    //    ------------------------------
    //    Helpers
    //    ------------------------------
    private void validateCommentReferences(long issueId) {
        EntityValidator.validateExists(issueRepository, issueId, "Issue");
    }

    private EntityNotFoundException notFound(long id) {
        return new EntityNotFoundException("Comment not found with id: %d".formatted(id));
    }

}
