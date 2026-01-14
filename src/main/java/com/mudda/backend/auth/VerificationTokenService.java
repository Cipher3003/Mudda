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

    //    TODO: remove generic day expiry
    @Transactional
    public VerificationToken generateToken(TokenType type, Long userId) {
        tokenRepository.deleteByUserIdAndType(userId, type);

        VerificationToken token = new VerificationToken(
                userId, UUID.randomUUID().toString(),
                type, Instant.now().plusMillis(Duration.ofDays(1).toMillis()));
        tokenRepository.save(token);

        return token;
    }

}
