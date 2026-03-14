package com.mudda.backend.comment;

import com.mudda.backend.comment.dto.CommentLikeResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentLikeService {

    private final CommentLikeRepository likeRepository;

    public CommentLikeService(CommentLikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    // #region Queries (Read Operations)

    public long countByCommentId(long commentId) {
        return likeRepository.countByCommentId(commentId);
    }

    // #endregion

    // #region Commands (Write Operations)

    @Transactional
    public CommentLikeResponse userLikesOnComment(long commentId, long userId) {
        CommentLike commentLike = new CommentLike(commentId, userId);
        likeRepository.save(commentLike);

        long likesCount = likeRepository.countByCommentId(commentId);

        return new CommentLikeResponse(true, likesCount);
    }

    public void saveCommentLikes(List<CommentLike> commentLikes) {
        likeRepository.saveAll(commentLikes);
    }

    @Transactional
    public CommentLikeResponse userRemovesLikeFromComment(long commentId, long userId) {
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);

        long likesCount = likeRepository.countByCommentId(commentId);

        return new CommentLikeResponse(false, likesCount);
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
