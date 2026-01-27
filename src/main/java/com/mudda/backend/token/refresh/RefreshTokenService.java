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
import com.mudda.backend.token.TokenHasUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenHasUtil tokenHasUtil;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               TokenHasUtil tokenHasUtil) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenHasUtil = tokenHasUtil;
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
        String hashedToken = tokenHasUtil.hashToken(rawRefreshToken);

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
        String hashedToken = tokenHasUtil.hashToken(rawRefreshToken);

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
                tokenHasUtil.hashToken(refreshToken),
                expiresAt);

        refreshTokenRepository.save(hashedRefreshToken);
        log.debug("Created refresh token for user {} valid till {}", userId, expiresAt);
    }

    // endregion

}
