package com.mudda.backend.postgres.models;

import com.mudda.backend.issue.IssueStatus;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.PrePersist;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "issue_logs")
// TODO: remove deprecated when build issue logs and depratment features
@Deprecated(forRemoval = false)
public class IssueLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long issueLogId;

    @Column(nullable = false)
    private Long issueId; // Reference to Issue in PostgreSQL

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueStatus status;

    @Column(nullable = false)
    private Long approvedBy; // Reference to User in PostgreSQL

    @Column(nullable = false)
    private String additionalNotes;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

}