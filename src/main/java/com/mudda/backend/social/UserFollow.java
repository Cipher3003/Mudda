package com.mudda.backend.social;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_follows", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"follower_id", "following_id"})
})
public class UserFollow {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_follow_seq")
    @SequenceGenerator(name = "user_follow_seq", sequenceName = "user_follow_id_seq", allocationSize = 50)
    @Column(name = "follow_id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "follower_id", nullable = false)
    private Long followerId;

    @Column(name = "following_id", nullable = false)
    private Long followingId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public UserFollow(Long followerId, Long followingId) {
        if (followerId == null || followingId == null) {
            throw new IllegalArgumentException("Follower and following IDs must not be null");
        }
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("User cannot follow themselves");
        }
        this.followerId = followerId;
        this.followingId = followingId;
    }
}
