/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : CommentLikeId
 * Author  : Vikas Kumar
 * Created : 18-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.comment;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class CommentLikeId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "user_id")
    private Long userId;

    public CommentLikeId(Long commentId, Long userId) {
        if (commentId == null || userId == null || commentId <= 0 || userId <= 0)
            throw new IllegalArgumentException("Comment and User ID's must be provided");

        this.commentId = commentId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CommentLikeId that)) return false;
        return Objects.equals(commentId, that.commentId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId, userId);
    }
}
