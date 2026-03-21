package com.mudda.backend.initiative;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "community_initiatives")
public class CommunityInitiative {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "initiatives_seq")
    @SequenceGenerator(name = "initiatives_seq", sequenceName = "initiatives_id_seq", allocationSize = 50)
    @Column(name = "initiative_id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "community_id", nullable = false)
    private Long communityId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "initiative_type", nullable = false)
    private InitiativeType type;

    @Column(name = "target_metric", nullable = false)
    private int targetMetric;

    @Column(name = "current_metric", nullable = false)
    private int currentMetric = 0;

    @Column(name = "event_date")
    private Instant eventDate;

    @Column(name = "active", nullable = false)
    private boolean active = true;

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

    public CommunityInitiative(Long communityId, String title, String description,
                               InitiativeType type, int targetMetric, Instant eventDate) {
        if (communityId == null)
            throw new IllegalArgumentException("Community ID must be provided");
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Initiative title cannot be empty");
        if (type == null)
            throw new IllegalArgumentException("Initiative type must be provided");
        if (targetMetric <= 0)
            throw new IllegalArgumentException("Target metric must be positive");

        this.communityId = communityId;
        this.title = title.trim();
        this.description = (description != null) ? description.trim() : null;
        this.type = type;
        this.targetMetric = targetMetric;
        this.eventDate = eventDate;
    }

    // ----- Domain Behaviour -------

    public void addProgress(int amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Progress amount must be positive");
        this.currentMetric += amount;
    }

    public void deactivate() {
        this.active = false;
    }

    public void updateDetails(String title, String description, Instant eventDate) {
        if (title != null && !title.isBlank())
            setTitle(title.trim());
        if (description != null)
            setDescription(description.trim());
        if (eventDate != null)
            setEventDate(eventDate);
    }

    public double getProgressPercentage() {
        if (targetMetric == 0) return 0.0;
        return Math.min(100.0, (currentMetric * 100.0) / targetMetric);
    }

    public boolean isComplete() {
        return currentMetric >= targetMetric;
    }
}
