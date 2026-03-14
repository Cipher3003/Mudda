package com.mudda.backend.comment.dto;

import com.mudda.backend.comment.CommentLike;

public record CommentLikeSeed(
        int commentId,
        int userId
) {

    public static CommentLike toCommentLike(CommentLikeSeed seed) {
        return new CommentLike(
                (long) seed.commentId(),
                (long) seed.userId()
        );
    }

}
