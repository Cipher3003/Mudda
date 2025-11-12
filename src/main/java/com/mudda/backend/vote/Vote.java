package com.mudda.backend.vote;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "Vote")
@Table(name = "votes")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id", updatable = false, nullable = false)
    private Long voteId;

    // Reference to Issue in PostgresSQL
    @Column(name = "issue_id", nullable = false)
    private Long issueId;

    // Reference to User in PostgresSQL
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

//    ----- Domain Constructor -----

    public Vote(Long issueId, Long userId) {
        if (issueId == null) throw new IllegalArgumentException("Issue Id cannot be null");
        if (userId == null) throw new IllegalArgumentException("User Id cannot be null");

        this.issueId = issueId;
        this.userId = userId;
    }

    // Factory method (DDD pattern)
    public static Vote castVote(Long issueId, Long userId) {
        return new Vote(issueId, userId);
    }

    //    TODO: remove unused methods
    // Domain methods for business logic
    public boolean isVotedBy(Long userId) {
        return this.userId.equals(userId);
    }

    public boolean isForIssue(Long issueId) {
        return this.issueId.equals(issueId);
    }
}
