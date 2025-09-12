package com.mudda.backend.comment;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(nullable = false)
    private String text;

    @Column
    private Long parentId;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Long issueId; // soft link to issue on which comment was made

    @Column(nullable = false)
    private Long userId; // soft link to user who made the comment on the Issue

    @PrePersist
    protected void onCreated() {
        createdAt = Instant.now();
    }
}
