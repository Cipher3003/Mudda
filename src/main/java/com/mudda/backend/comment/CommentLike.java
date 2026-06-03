package com.mudda.backend.comment;

import com.mudda.backend.user.MuddaUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity(name = "CommentLike")
@Table(name = "comment_likes", schema = "civic")
public class CommentLike {

    @EmbeddedId
    private CommentLikeId id;

    @Column(nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", updatable = false, insertable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private MuddaUser user;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    // ----- Domain Constructor -----

    public CommentLike(Long commentId, Long userId) {
        this.id = new CommentLikeId(commentId, userId);
    }
}
