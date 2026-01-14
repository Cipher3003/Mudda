/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : VerificationTokenService
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.auth;

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

    // #region Commands (Write Operations)

    //    TODO: modify messages to use message codes and util
    @Transactional
    public VerificationToken consumeToken(String verificationToken, TokenType tokenType) {
        VerificationToken token = tokenRepository.findByToken(verificationToken)
                .orElseThrow(() -> new InvalidTokenException("Token not found"));

        if (token.isUsed())
            throw new InvalidTokenException("Token already used.");

        if (token.getExpiryAt().isBefore(Instant.now()))
            throw new InvalidTokenException("Token is expired.");

        if (!token.getType().equals(tokenType))
            throw new InvalidTokenException("Invalid token type.");

        token.markUsed();
        tokenRepository.save(token);
        return token;
    }

    // #endregion

    // #region Commands (Write Operations)

    //    TODO: remove generic day expiry
    @Transactional
    public VerificationToken generateToken(TokenType type, Long userId) {
        tokenRepository.deleteByUserIdAndType(userId, type);

        VerificationToken token = new VerificationToken(
                userId,
                UUID.randomUUID().toString(),
                type,
                Instant.now().plus(Duration.ofDays(1)));

        return tokenRepository.save(token);
    }

    // #endregion

}
