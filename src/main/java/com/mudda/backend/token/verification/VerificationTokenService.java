/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : VerificationTokenService
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.token.verification;

import com.mudda.backend.exceptions.InvalidVerificationTokenException;
import com.mudda.backend.exceptions.TokenFailureReason;
import com.mudda.backend.exceptions.TokenValidationException;
import com.mudda.backend.token.TokenType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;

    public VerificationTokenService(VerificationTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    // #region Queries (Read Operations)

    public boolean recentTokenExists(Long userId, TokenType tokenType, Duration cooldown) {
        return tokenRepository.existsByUserIdAndTypeAndCreatedAtAfter(
                userId, tokenType, Instant.now().minus(cooldown)
        );
    }

    // #endregion

    // #region Commands (Write Operations)

    //    TODO: should delete all consumed tokens periodically
    @Transactional
    public VerificationToken consumeToken(String verificationToken, TokenType tokenType) {
        VerificationToken token = tokenRepository.findByToken(verificationToken)
                .orElseThrow(InvalidVerificationTokenException::new);

        if (!token.getType().equals(tokenType))
            throw new InvalidVerificationTokenException();

        if (token.getUsedAt() != null)
            throw new TokenValidationException(tokenType, TokenFailureReason.ALREADY_USED);

        if (token.getExpiresAt().isBefore(Instant.now()))
            throw new TokenValidationException(tokenType, TokenFailureReason.EXPIRED);

        tokenRepository.markUsed(token.getId());
        return token;
    }

    //    TODO: remove generic day expiry
    @Transactional
    public VerificationToken generateToken(TokenType type, Long userId) {

        VerificationToken token = new VerificationToken(
                userId,
                UUID.randomUUID().toString(),
                type,
                Instant.now().plus(Duration.ofDays(1)));

        return tokenRepository.save(token);
    }

    @Transactional
    public void invalidateUnusedTokens(Long userId, TokenType type) {
        tokenRepository.invalidateUnusedTokens(userId, type);
    }

    @Transactional
    public void deleteAllTokensByUserId(Long userId) {
        tokenRepository.deleteAllByUserId(userId);
    }

    // #endregion

}
