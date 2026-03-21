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
@Table(name = "initiative_participants",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_initiative_participant_user",
                columnNames = {"user_id", "initiative_id"}))
public class InitiativeParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "initiative_participants_seq")
    @SequenceGenerator(name = "initiative_participants_seq", sequenceName = "initiative_participants_id_seq", allocationSize = 50)
    @Column(name = "participant_id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "initiative_id", nullable = false)
    private Long initiativeId;

    @Column(name = "pledged_metric", nullable = false)
    private int pledgedMetric;

    @Column(name = "participated_at", nullable = false)
    private Instant participatedAt;

    @PrePersist
    protected void onCreate() {
        participatedAt = Instant.now();
    }

    // ----- Domain Constructor -----

    public InitiativeParticipant(Long userId, Long initiativeId, int pledgedMetric) {
        if (userId == null)
            throw new IllegalArgumentException("User ID must be provided");
        if (initiativeId == null)
            throw new IllegalArgumentException("Initiative ID must be provided");
        if (pledgedMetric <= 0)
            throw new IllegalArgumentException("Pledged metric must be positive");

        this.userId = userId;
        this.initiativeId = initiativeId;
        this.pledgedMetric = pledgedMetric;
    }
}
