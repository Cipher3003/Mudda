/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : RefreshToken
 * Author  : Vikas Kumar
 * Created : 13-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.token.refresh;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "RefreshToken")
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_refresh_token_token", columnList = "token"),
        @Index(name = "idx_refresh_token_user_id", columnList = "user_id")
})
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refresh_token_seq")
    @SequenceGenerator(name = "refresh_token_seq", sequenceName = "refresh_token_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "token", nullable = false, length = 64)
    private String token;

    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public RefreshToken(Long userId, String token, Instant expiresAt) {

        if (userId == null)
            throw new IllegalArgumentException("Refresh Token userId cannot be null");
        if (token == null || token.isBlank())
            throw new IllegalArgumentException("Refresh token token cannot be blank");
        if (expiresAt == null || expiresAt.isBefore(Instant.now()))
            throw new IllegalArgumentException("Refresh Token expiration time cannot be negative");

        this.userId = userId;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public void revoke() {
        revoked = true;
    }

}
