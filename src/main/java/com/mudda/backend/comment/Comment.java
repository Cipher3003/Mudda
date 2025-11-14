package com.mudda.backend.comment;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity(name = "Comment")
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column
    private Long parentId;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Long issueId; // soft link to issue on which comment was made

    @Column(nullable = false)
    private Long userId; // soft link to user who made the comment on the Issue

    @PrePersist
    protected void onCreated() {
        createdAt = Instant.now();
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
