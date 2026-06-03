package com.mudda.backend.comment;

import java.time.Instant;

import com.mudda.backend.issue.Issue;
import com.mudda.backend.user.MuddaUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity(name = "Comment")
@Table(name = "comments", schema = "civic")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comments_seq")
    @SequenceGenerator(name = "comments_seq", sequenceName = "comments_id_seq")
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(nullable = false)
    private long likeCount = 0;

    @Column(nullable = false)
    private long replyCount = 0;

    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    @Column
    private Instant deletedAt;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "issue_id", nullable = false)
    private Long issueId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", updatable = false, insertable = false)
    private Issue issue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private MuddaUser author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", updatable = false, insertable = false)
    private Comment parentComment;

    @PrePersist
    protected void onCreated() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdated() {
        updatedAt = Instant.now();
    }

    // ----- Domain Constructor -----

    public Comment(String text, Long issueId, Long userId) {

        if (text == null || text.isBlank())
            throw new IllegalArgumentException("Comment text cannot be empty");
        if (issueId == null || userId == null)
            throw new IllegalArgumentException("Issue and User ID's must be provided for Comment");

        this.text = text.trim();
        this.parentId = null;
        this.issueId = issueId;
        this.userId = userId;
    }

    public Comment(String text, Long parentId, Long issueId, Long userId) {

        if (text == null || text.isBlank())
            throw new IllegalArgumentException("Comment text cannot be empty");
        if (parentId == null || issueId == null || userId == null)
            throw new IllegalArgumentException("Parent, Issue and User ID's must be provided for Reply");

        this.text = text.trim();
        this.parentId = parentId;
        this.issueId = issueId;
        this.userId = userId;
    }

    // ----- Domain Behaviour -------

    public void updateDetails(String text) {
        if (text != null && !text.isBlank())
            setText(text.trim());
    }
}
