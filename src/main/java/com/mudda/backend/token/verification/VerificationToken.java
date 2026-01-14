/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : VerificationToken
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.token.verification;

import com.mudda.backend.token.TokenType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "VerificationToken")
@Table(name = "action_tokens", indexes = {
        @Index(name = "idx_action_tokens_token", columnList = "token"),
        @Index(name = "idx_action_tokens_user_type", columnList = "userId,type")
})
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "action_tokens_seq")
    @SequenceGenerator(name = "action_tokens_seq", sequenceName = "action_tokens_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType type;

    @Column(nullable = false)
    private Instant expiryAt;

    @Column(nullable = false)
    private boolean used = false;

    public VerificationToken(Long userId, String token, TokenType type, Instant expiryAt) {
        if (userId == null)
            throw new IllegalArgumentException("Verification Token userId cannot be null");
        if (token == null || token.isBlank())
            throw new IllegalArgumentException("Verification token token cannot be blank");
        if (expiryAt == null || expiryAt.isBefore(Instant.now()))
            throw new IllegalArgumentException("Verification Token expiry must be in future");

        this.userId = userId;
        this.token = token;
        this.type = type;
        this.expiryAt = expiryAt;
    }

    public void markUsed() {
        this.used = true;
    }
}
