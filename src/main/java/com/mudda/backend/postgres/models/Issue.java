package com.mudda.backend.postgres.models;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

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
    private Long id;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private IssueStatus status = IssueStatus.OPEN;

    private List<String> mediaUrls;

    private Instant createdAt = Instant.now();
    private Instant updatedAt;

    @Column(nullable = false)
    private Long userId; // Reference to User in PostgreSQL
    @Column(nullable = false)
    private Long locationId; // location of the issue raised
    @Column(nullable = false)
    private Long categoryId; // Reference to Category in PostgreSQL
}