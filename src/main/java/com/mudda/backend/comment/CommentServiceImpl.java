package com.mudda.backend.comment;

import com.mudda.backend.issue.IssueService;
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

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeService commentLikeService;
    private final CommentLikeRepository commentLikeRepository;
    private final IssueService issueService;

    public CommentServiceImpl(CommentRepository commentRepository,
                              CommentLikeService commentLikeService,
                              CommentLikeRepository commentLikeRepository,
                              IssueService issueService) {
        this.commentRepository = commentRepository;
        this.commentLikeService = commentLikeService;
        this.commentLikeRepository = commentLikeRepository;
        this.issueService = issueService;
    }

    // #region Queries (Read Operations)

    @Override
    public Page<CommentDetailResponse> findCommentsWithLikes(long issueId, Pageable pageable, long userId) {
        Page<Comment> commentPage = commentRepository.findByIssueIdAndParentIdIsNull(issueId, pageable);

        List<Long> ids = commentPage.getContent()
                .stream()
                .map(Comment::getCommentId)
                .toList();

        Set<Long> likedIds = commentLikeRepository.findByUserIdAndCommentIdIn(userId, ids)
                .stream()
                .map(CommentLike::getCommentId)
                .collect(Collectors.toSet()); // Uses Set to keep lookup fast for mapping in below function

        return commentPage
                .map(comment -> getCommentResponseFromComment(comment, likedIds.contains(comment.getCommentId())));
    }

    @Override
    public Optional<CommentDetailResponse> findById(long commentId, long userId) {
        boolean hasUserLiked = commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
        return commentRepository
                .findById(commentId)
                .map(comment -> getCommentResponseFromComment(comment, hasUserLiked));
    }

    @Override
    public Page<ReplyResponse> findAllReplies(long parentId, Pageable pageable, long userId) {
        Page<Comment> replyPage = commentRepository.findByParentId(parentId, pageable);

        List<Long> ids = replyPage.getContent()
                .stream()
                .map(Comment::getCommentId)
                .toList();

        Set<Long> likedIds = commentLikeRepository.findByUserIdAndCommentIdIn(userId, ids)
                .stream()
                .map(CommentLike::getCommentId)
                .collect(Collectors.toSet()); // Uses Set to keep lookup fast for mapping in below function

        return replyPage
                .map(comment -> getReplyResponseFromComment(comment, likedIds.contains(comment.getCommentId())));
    }

    private CommentDetailResponse getCommentResponseFromComment(Comment comment, boolean hasUserLiked) {
        long likes = commentLikeService.countByCommentId(comment.getCommentId());
        long replies = commentRepository.countByParentId(comment.getCommentId());
        return CommentMapper.toCommentResponse(comment, likes, replies, hasUserLiked);
    }

    private ReplyResponse getReplyResponseFromComment(Comment comment, boolean hasUserLiked) {
        long likes = commentLikeService.countByCommentId(comment.getCommentId());
        return CommentMapper.toReplyResponse(comment, likes, hasUserLiked);
    }

    // #endregion

    // #region Commands (Write Operations)

    @Transactional
    @Override
    public CommentResponse createComment(long issueId, CreateCommentRequest createCommentRequest) {
        // TODO: maybe use exitsBy to validate issueId
        issueService.findById(issueId).orElseThrow(
                () -> new IllegalArgumentException("Issue with id: %d not found".formatted(issueId)));

        Comment comment = CommentMapper.toComment(createCommentRequest, issueId);
        Comment saved = commentRepository.save(comment);
        return CommentMapper.toCommentResponse(saved);
    }

    @Transactional
    @Override
    public CommentResponse createReply(long parentId, CreateCommentRequest createCommentRequest) {
        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Comment with id: %d does not exists.".formatted(parentId)));

        Comment reply = CommentMapper.toReply(createCommentRequest, parent.getIssueId(), parentId);
        Comment saved = commentRepository.save(reply);
        return CommentMapper.toCommentResponse(saved);
    }

    @Transactional
    @Override
    public CommentResponse updateComment(long id, String text) {
        if (text == null || text.isBlank())
            throw new IllegalArgumentException("Comment text cannot be empty.");

        Comment comment = commentRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        comment.updateDetails(text);
        Comment saved = commentRepository.save(comment);
        return CommentMapper.toCommentResponse(saved);
    }

    @Transactional
    @Override
    public void deleteComment(long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

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
    public void deleteAllCommentsByIssueId(long issueId) {
        // Parent Comments
        List<Long> commentIds = commentRepository.findByIssueId(issueId)
                .stream()
                .map(Comment::getCommentId)
                .toList();

        if (commentIds.isEmpty())
            return;

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
    public CommentLikeResponse likeComment(long commentId, long userId) {
        return commentLikeService.userLikesOnComment(commentId, userId);
    }

    @Transactional
    @Override
    public CommentLikeResponse deleteLikeComment(long commentId, long userId) {
        return commentLikeService.userRemovesLikeFromComment(commentId, userId);
    }

    // #endregion

}
