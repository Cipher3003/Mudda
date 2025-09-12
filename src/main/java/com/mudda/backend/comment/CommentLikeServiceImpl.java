package com.mudda.backend.comment;

import org.springframework.stereotype.Service;

@Service
public class CommentLikeServiceImpl implements CommentLikeService {

    private final CommentLikeRepository likeRepository;

    public CommentLikeServiceImpl(CommentLikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @Override
    public boolean hasUserLikedOnComment(Long commentId, Long userId) {
        return likeRepository.existsByCommentIdAndUserId(commentId, userId);
    }

    @Override
    public CommentLike userLikesOnComment(Long commentId, Long userId) {
        CommentLike commentLike = new CommentLike();
        return likeRepository.save(commentLike);
    }

    @Override
    public void userRemovesLikeFromComment(Long commentId, Long userId) {
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }
}
