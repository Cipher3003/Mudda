package com.mudda.backend.comment;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity(name = "CommentLike")
@Table(name = "comment_likes")
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private Long commentId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

//    ----- Domain Constructor -----

    public CommentLike(Long commentId, Long userId) {
        if (commentId == null || userId == null)
            throw new IllegalArgumentException("Comment and User ID's must be provided");

        this.commentId = commentId;
        this.userId = userId;
    }
}
