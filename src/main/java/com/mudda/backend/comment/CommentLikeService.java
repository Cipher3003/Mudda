package com.mudda.backend.comment;

import com.mudda.backend.comment.dto.CommentLikeResponse;
import com.mudda.backend.comment.event.CommentLikeCreatedEvent;
import com.mudda.backend.comment.event.CommentLikeRemovedEvent;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentLikeService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository likeRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public CommentLikeService(
            CommentRepository commentRepository,
            CommentLikeRepository likeRepository,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    // #region Queries (Read Operations)

    public long countByCommentId(long commentId) {
        return likeRepository.countByCommentId(commentId);
    }

    // #endregion

    // #region Commands (Write Operations)

    @Transactional
    public CommentLikeResponse likeOnComment(long commentId, long userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Like on Comment: %d not found".formatted(commentId))
        );

        CommentLike commentLike = new CommentLike(commentId, userId);
        likeRepository.save(commentLike);

        applicationEventPublisher.publishEvent(new CommentLikeCreatedEvent(comment.getId()));

        return new CommentLikeResponse(true, comment.getLikeCount() + 1);
    }

    public void saveCommentLikes(List<CommentLike> commentLikes) {
        likeRepository.saveAll(commentLikes);
    }

    @Transactional
    public CommentLikeResponse deleteLikeFromComment(long commentId, long userId) {
        // TODO: optimize select to only take metrics
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Like on Comment: %d not found".formatted(commentId))
        );

        likeRepository.deleteByCommentIdAndUserId(commentId, userId);

        applicationEventPublisher.publishEvent(new CommentLikeRemovedEvent(comment.getId()));

        return new CommentLikeResponse(false, comment.getLikeCount() - 1);
    }

    @Transactional
    public void deleteAllFromTopLevelComment(long commentId) {
        likeRepository.deleteFromTopLevelComment(commentId);
    }

    @Transactional
    public void deleteAllByCommentId(List<Long> commentIds) {
        likeRepository.deleteAllByCommentIdIn(commentIds);
    }

    @Transactional
    public void deleteByCommentId(long commentId) {
        likeRepository.deleteByCommentId(commentId);
    }

    @Transactional
    public void deleteAllByUserId(long userId) {
        likeRepository.deleteByUserId(userId);
    }

    // #endregion
}
