package com.mudda.backend.comment;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CommentLikeServiceImpl implements CommentLikeService {

    private final CommentLikeRepository likeRepository;

    public CommentLikeServiceImpl(CommentLikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    // #region Queries (Read Operations)

    @Override
    public long countByCommentId(long commentId) {
        return likeRepository.countByCommentId(commentId);
    }

    // #endregion

    // #region Commands (Write Operations)

    @Transactional
    @Override
    public CommentLikeResponse userLikesOnComment(long commentId, long userId) {
        CommentLike commentLike = new CommentLike(commentId, userId);
        likeRepository.save(commentLike);

        long likesCount = likeRepository.countByCommentId(commentId);

        return new CommentLikeResponse(true, likesCount);
    }

    @Override
    public void saveCommentLikes(List<CommentLike> commentLikes) {
        likeRepository.saveAll(commentLikes);
    }

    @Transactional
    @Override
    public CommentLikeResponse userRemovesLikeFromComment(long commentId, long userId) {
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);

        long likesCount = likeRepository.countByCommentId(commentId);

        return new CommentLikeResponse(false, likesCount);
    }

    @Transactional
    @Override
    public void deleteAllByCommentId(List<Long> commentIds) {
        likeRepository.deleteAllByCommentIdIn(commentIds);
    }

    @Transactional
    @Override
    public void deleteByCommentId(long commentId) {
        likeRepository.deleteByCommentId(commentId);
    }

    @Transactional
    @Override
    public void deleteAllByUserId(long userId) {
        likeRepository.deleteByUserId(userId);
    }

    // #endregion
}
