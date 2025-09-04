package com.mudda.backend.postgres.models;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reply_likes")
public class ReplyLike {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private Long replyId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}
