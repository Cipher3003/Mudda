/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : RefreshTokenService
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.token.refresh;

import com.mudda.backend.exceptions.InvalidRefreshTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;

@Slf4j
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // region Commands (Write Operations)

    @Transactional
    public void revokeAllByUserId(Long userId) {
        int count = refreshTokenRepository.revokeByUserId(userId);
        log.debug("Revoked {} refresh tokens assigned to user {}", count, userId);
    }

    @Transactional
    public RefreshToken rotate(String rawRefreshToken) {
        String hashedToken = hashToken(rawRefreshToken);
        Instant now = Instant.now();

        RefreshToken refreshToken = refreshTokenRepository.
                findByToken(hashedToken)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (refreshToken.isRevoked()) {
            revokeAllByUserId(refreshToken.getUserId());
            throw new InvalidRefreshTokenException();
        }

        if (refreshToken.getExpiresAt().isBefore(now))
            throw new InvalidRefreshTokenException();

        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);
        log.debug("Rotated refresh token");

        return refreshToken;
    }

    @Transactional
    public void revoke(String rawRefreshToken) {
        String hashedToken = hashToken(rawRefreshToken);
        int count = refreshTokenRepository.revokeByToken(hashedToken);
        log.debug("Revoked {} refresh tokens", count);
    }

    @Transactional
    public void create(Long userId, String refreshToken, Instant expiresAt) {
        RefreshToken hashedRefreshToken = new RefreshToken(
                userId,
                hashToken(refreshToken),
                expiresAt
        );

        refreshTokenRepository.save(hashedRefreshToken);
        log.debug("Created refresh token for user {} valid till {}", userId, expiresAt);
    }

    // endregion

    // region Helper

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not found", e);
        }
    }

    // endregion

}
