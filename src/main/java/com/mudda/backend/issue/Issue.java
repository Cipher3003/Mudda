package com.mudda.backend.issue;

import com.mudda.backend.user.MuddaUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.time.Instant;

// TODO: implement reporting system

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "Issue")
@Table(
        name = "issues",
        schema = "civic",
        indexes = @Index(name = "idx_issues_deleted_at", columnList = "deleted_at")
)
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "issues_seq")
    @SequenceGenerator(name = "issues_seq", sequenceName = "issues_id_seq")
    @Column(name = "issue_id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "pin_code", nullable = false)
    private String pinCode;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "coordinate", columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point coordinate;

    @Enumerated(EnumType.STRING)
    @Column(name = "issue_status", nullable = false)
    private IssueStatus status = IssueStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(name = "issue_category", length = 4, nullable = false)
    private IssueCategory category;

    @Column(name = "severity_score", nullable = false)
    private double severityScore = 0.0;

    @Column(name = "vote_count", nullable = false)
    private long voteCount = 0;

    @Column(name = "comment_count", nullable = false)
    private long commentCount = 0;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private MuddaUser author;

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

    public Issue(
            String title,
            String description,
            Long userId,
            IssueCategory category,
            String pinCode,
            String city,
            String state,
            Point coordinate
    ) {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Issue title cannot be empty");
        if (description == null || description.isBlank())
            throw new IllegalArgumentException("Issue description cannot be empty");
        if (userId == null || category == null)
            throw new IllegalArgumentException("User id and must be provided");
        if (pinCode == null || pinCode.isBlank())
            throw new IllegalArgumentException("PinCode cannot be empty");
        if (city == null || city.isBlank())
            throw new IllegalArgumentException("City cannot be empty");
        if (state == null || state.isBlank())
            throw new IllegalArgumentException("State cannot be empty");
        if (coordinate == null || coordinate.isEmpty())
            throw new IllegalArgumentException("Coordinate cannot be empty");

        this.title = title.trim();
        this.description = description.trim();
        this.userId = userId;
        this.category = category;
        this.pinCode = pinCode;
        this.city = city;
        this.state = state;
        this.coordinate = coordinate;
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