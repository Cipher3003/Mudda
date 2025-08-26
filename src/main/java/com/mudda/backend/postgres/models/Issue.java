package com.mudda.backend.postgres.models;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import org.hibernate.annotations.Type;
import com.vladmihalcea.hibernate.type.array.ListArrayType;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "issues")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long issueId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueStatus status = IssueStatus.OPEN;

    @Column(nullable = false)
    private Long userId; // Reference to User in PostgreSQL

    @Column(nullable = false)
    private Long locationId; // location of the issue raised

    @Column(nullable = false)
    private Long categoryId; // Reference to Category in PostgreSQL

    // Media URLs stored as a Postgres text[] array
    @Type(ListArrayType.class)
    @Column(name = "media_urls", columnDefinition = "text[]")
    private List<String> mediaUrls;

    // Flags
    @Column(nullable = false)
    private boolean deleteFlag = false;

    @Column(nullable = false)
    private boolean reportFlag = false;

    @Column(nullable = false)
    private boolean urgencyFlag = false;

    // Severity score
    @Column(nullable = false)
    private float severityScore = 0.0f;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}