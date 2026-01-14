/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : RefreshTokenService
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.token.refresh;

import com.mudda.backend.auth.InvalidRefreshTokenException;
import com.mudda.backend.token.TokenHasUtil;
import com.mudda.backend.utils.MessageCodes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenHasUtil tokenHasUtil;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               TokenHasUtil tokenHasUtil) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenHasUtil = tokenHasUtil;
    }

    // #region Commands (Write Operations)

    @Transactional
    public void revokeAllByUserId(Long userId) {
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByUserId(userId);
        for (RefreshToken refreshToken : refreshTokens) {
            refreshToken.revoke();
        }
        refreshTokenRepository.saveAll(refreshTokens);
    }

    @Transactional
    public RefreshToken rotate(String rawRefreshToken) {
        String hashedToken = tokenHasUtil.hashToken(rawRefreshToken);

        RefreshToken refreshToken = refreshTokenRepository.
                findByTokenAndRevokedFalse(hashedToken)
                .orElseThrow(() -> new InvalidRefreshTokenException(MessageCodes.INVALID_REFRESH_TOKEN));

        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    @Transactional
    public void revoke(String rawRefreshToken) {
        String hashedToken = tokenHasUtil.hashToken(rawRefreshToken);

        refreshTokenRepository.findByTokenAndRevokedFalse(hashedToken)
                .ifPresent(refreshToken -> {
                    refreshToken.revoke();
                    refreshTokenRepository.save(refreshToken);
                });
    }

    @Transactional
    public void create(Long userId, String refreshToken, long ttlMs) {
        RefreshToken hashedRefreshToken = new RefreshToken(
                userId,
                tokenHasUtil.hashToken(refreshToken),
                ttlMs);

        refreshTokenRepository.save(hashedRefreshToken);
    }

    // #endregion

}
