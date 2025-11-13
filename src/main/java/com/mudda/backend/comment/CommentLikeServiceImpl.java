package com.mudda.backend.comment;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CommentLikeServiceImpl implements CommentLikeService {

    private final CommentLikeRepository likeRepository;

    public CommentLikeServiceImpl(CommentLikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @Override
    public CommentLikeResponse userLikesOnComment(long commentId, long userId) {
        CommentLike commentLike = new CommentLike();
        commentLike.setCommentId(commentId);
        commentLike.setUserId(userId);
        likeRepository.save(commentLike);
        long likesCount = likeRepository.countByCommentId(commentId);
        return new CommentLikeResponse(true, likesCount);
    }

    @Override
    public CommentLikeResponse userRemovesLikeFromComment(long commentId, long userId) {
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
        long likesCount = likeRepository.countByCommentId(commentId);
        return new CommentLikeResponse(false, likesCount);
    }

    @Override
    public long countByCommentId(long commentId) {
        return likeRepository.countByCommentId(commentId);
    }

    @Override
    public void deleteAllByCommentId(List<Long> commentIds) {
        likeRepository.deleteAllByCommentIdIn(commentIds);
    }

    @Override
    public void deleteByCommentId(long commentId) {
        likeRepository.deleteByCommentId(commentId);
    }

    @Override
    public void deleteAllByUserId(long userId) {
        likeRepository.deleteByUserId(userId);
    }
}
