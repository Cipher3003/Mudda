package com.mudda.backend.comment;

import java.util.List;

public interface CommentLikeService {

    CommentLikeResponse userLikesOnComment(long commentId, long userId);

    CommentLikeResponse userRemovesLikeFromComment(long commentId, long userId);

    long countByCommentId(long commentId);

    void saveCommentLikes(List<CommentLike> commentLikes);

    void deleteAllByCommentId(List<Long> replyIds);

    void deleteByCommentId(long commentId);

    void deleteAllByUserId(long userId);
}
