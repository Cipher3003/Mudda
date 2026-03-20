package com.mudda.backend.community;

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
@Table(name = "community_members",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_community_member_user",
                columnNames = {"user_id", "community_id"}))
public class CommunityMember {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "community_members_seq")
    @SequenceGenerator(name = "community_members_seq", sequenceName = "community_members_id_seq", allocationSize = 50)
    @Column(name = "member_id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "community_id", nullable = false)
    private Long communityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "community_role", nullable = false)
    private CommunityRole role = CommunityRole.RESIDENT;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_status", nullable = false)
    private MemberStatus status = MemberStatus.PENDING;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    @PrePersist
    protected void onCreate() {
        joinedAt = Instant.now();
    }

    // ----- Domain Constructor -----

    public CommunityMember(Long userId, Long communityId) {
        if (userId == null)
            throw new IllegalArgumentException("User ID must be provided");
        if (communityId == null)
            throw new IllegalArgumentException("Community ID must be provided");

        this.userId = userId;
        this.communityId = communityId;
    }

    /**
     * Constructor for seeding / admin-created memberships with a specific role.
     */
    public CommunityMember(Long userId, Long communityId, CommunityRole role, MemberStatus status) {
        this(userId, communityId);
        this.role = role;
        this.status = status;
    }

    // ----- Domain Behaviour -------

    public void verify() {
        this.status = MemberStatus.VERIFIED;
    }

    public void reject() {
        this.status = MemberStatus.REJECTED;
    }

    public void promoteToModerator() {
        this.role = CommunityRole.MODERATOR;
    }

    public void promoteToAdmin() {
        this.role = CommunityRole.ADMIN;
    }

    public boolean isVerified() {
        return this.status == MemberStatus.VERIFIED;
    }

    public boolean isAdmin() {
        return this.role == CommunityRole.ADMIN;
    }
}
