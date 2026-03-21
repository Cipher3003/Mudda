package com.mudda.backend.community.announcement;

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
@Table(name = "community_announcements")
public class CommunityAnnouncement {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "announcements_seq")
    @SequenceGenerator(name = "announcements_seq", sequenceName = "announcements_id_seq", allocationSize = 50)
    @Column(name = "announcement_id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "community_id", nullable = false)
    private Long communityId;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "body", columnDefinition = "TEXT", nullable = false)
    private String body;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    // ----- Domain Constructor -----

    public CommunityAnnouncement(Long communityId, Long authorId, String title, String body) {
        if (communityId == null)
            throw new IllegalArgumentException("Community ID must be provided");
        if (authorId == null)
            throw new IllegalArgumentException("Author ID must be provided");
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Announcement title cannot be empty");
        if (body == null || body.isBlank())
            throw new IllegalArgumentException("Announcement body cannot be empty");

        this.communityId = communityId;
        this.authorId = authorId;
        this.title = title.trim();
        this.body = body.trim();
    }
}
