/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : Media
 * Author  : Vikas Kumar
 * Created : 16-03-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.media;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "Media")
@Table(
        name = "media",
        indexes = {
                @Index(name = "idx_media_ownerType_and_ownerId", columnList = "owner_type, owner_id"),
                @Index(name = "idx_media_public_id", columnList = "public_id")
        }
)
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "media_seq")
    @SequenceGenerator(name = "media_seq", sequenceName = "media_id_seq")
    @Column(name = "id", nullable = false, updatable = false)
    private long id;

    @Column(name = "public_id", nullable = false, updatable = false, unique = true, length = 36)
    private String publicId;

    @Column(name = "media_key", nullable = false)
    private String mediaKey;

    @Column(name = "size")
    private Long size;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UploadStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type")
    private MediaOwner ownerType;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public Media(String publicId, String mediaKey, UploadStatus status, Integer position) {
        if (publicId == null || publicId.isBlank())
            throw new IllegalArgumentException("Public ID cannot be null or blank");
        if (mediaKey == null || mediaKey.isEmpty())
            throw new IllegalArgumentException("mediaKey cannot be null or empty");
        if (status == null)
            throw new IllegalArgumentException("status cannot be null");
        if (position == null || position < 0)
            throw new IllegalArgumentException("position cannot be null or negative");

        this.publicId = publicId;
        this.mediaKey = mediaKey;
        this.status = status;
        this.position = position;
    }

    public void assignOwner(MediaOwner ownerType, Long ownerId) {
        if (ownerType == null)
            throw new IllegalArgumentException("owner cannot be null");
        if (ownerId == null || ownerId < 0)
            throw new IllegalArgumentException("ownerId cannot be null or negative");

        this.ownerType = ownerType;
        this.ownerId = ownerId;
    }

    public void updateOrder(Integer position) {
        if (position == null || position < 0)
            throw new IllegalArgumentException("position cannot be null or negative");
        this.position = position;
    }
}
