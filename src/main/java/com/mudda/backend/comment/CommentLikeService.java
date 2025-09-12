package com.mudda.backend.comment;

public interface CommentLikeService {

    boolean hasUserLikedOnComment(Long commentId, Long userId);

    CommentLike userLikesOnComment(Long commentId, Long userId);

    void userRemovesLikeFromComment(Long commentId, Long userId);
}
