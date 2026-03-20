package com.mudda.backend.vote;

import com.mudda.backend.issue.Issue;
import com.mudda.backend.user.MuddaUser;
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

    @EmbeddedId
    private VoteId id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private MuddaUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", updatable = false, insertable = false)
    private Issue issue;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    // ----- Domain Constructor -----

    public Vote(Long issueId, Long userId) {
        this.id = new VoteId(issueId, userId);
    }

    // Factory method (DDD pattern)
    public static Vote castVote(Long issueId, Long userId) {
        return new Vote(issueId, userId);
    }
}
