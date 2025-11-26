package com.mudda.backend.issue;

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
import jakarta.persistence.SequenceGenerator;
import lombok.*;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.Type;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "Issue")
@Table(name = "issues")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "issues_seq")
    @SequenceGenerator(name = "issues_seq", sequenceName = "issues_id_seq", allocationSize = 50)
    @Column(name = "issue_id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "issue_status", nullable = false)
    private IssueStatus status = IssueStatus.OPEN;

    // Reference to User in PostgresSQL
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // location of the issue raised
    @Column(name = "location_id", nullable = false)
    private Long locationId;

    // Reference to Category in PostgresSQL
    @Column(name = "issue_category_id", nullable = false)
    private Long categoryId;

    // Media URLs stored as a Postgres text[] array
    @Type(ListArrayType.class)
    @Column(name = "media_urls", columnDefinition = "text[]")
    private List<String> mediaUrls;

    // Flags
    @Column(name = "delete_flag", nullable = false)
    private boolean deleteFlag = false;

    // TODO: uncomment when implement reporting
    // @Column(nullable = false)
    // private boolean reportFlag = false;

    @Column(name = "urgency_flag", nullable = false)
    private boolean urgencyFlag = false;

    // Severity score
    @Column(name = "severity_score", nullable = false)
    private double severityScore = 0.0;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
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

    // ----- Domain Constructor -----

    public Issue(String title,
            String description,
            Long userId,
            Long locationId,
            Long categoryId,
            List<String> mediaUrls) {

        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Issue title cannot be empty");
        if (description == null || description.isBlank())
            throw new IllegalArgumentException("Issue description cannot be empty");
        if (userId == null || locationId == null || categoryId == null)
            throw new IllegalArgumentException("User, category and locations ids must be provided");

        this.title = title.trim();
        this.description = description.trim();
        this.userId = userId;
        this.locationId = locationId;
        this.categoryId = categoryId;
        if (mediaUrls == null || mediaUrls.isEmpty()) {
            this.mediaUrls = List.of();
        } else {
            this.mediaUrls = (mediaUrls.size() > 5)
                    ? mediaUrls.subList(0, 5).stream().toList()
                    : List.copyOf(mediaUrls);
        }
    }

    // ----- Domain Behaviour -------

    public void updateDetails(String title, String description, IssueStatus status) {
        if (title != null && !title.isBlank())
            setTitle(title.trim());
        if (description != null && !description.isBlank())
            setDescription(description.trim());
        if (status != null)
            setStatus(status);
    }
}