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
import java.util.List;

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
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByUserId(userId);
        for (RefreshToken refreshToken : refreshTokens) {
            refreshToken.revoke();
        }
        refreshTokenRepository.saveAll(refreshTokens);
        log.debug("Revoked all refresh tokens assigned to user {}", userId);
    }

    @Transactional
    public RefreshToken rotate(String rawRefreshToken) {
        String hashedToken = hashToken(rawRefreshToken);

        RefreshToken refreshToken = refreshTokenRepository.
                findByTokenAndRevokedFalse(hashedToken)
                .orElseThrow(InvalidRefreshTokenException::new);

        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);
        log.debug("Rotated refresh token");

        return refreshToken;
    }

    @Transactional
    public void revoke(String rawRefreshToken) {
        String hashedToken = hashToken(rawRefreshToken);

        log.debug("Revoking refresh token");
        refreshTokenRepository.findByTokenAndRevokedFalse(hashedToken)
                .ifPresent(refreshToken -> {
                    refreshToken.revoke();
                    refreshTokenRepository.save(refreshToken);
                });
    }

    @Transactional
    public void create(Long userId, String refreshToken, Instant expiresAt) {
        RefreshToken hashedRefreshToken = new RefreshToken(
                userId,
                hashToken(refreshToken),
                expiresAt);

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
