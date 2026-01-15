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

    // #region Commands (Write Operations)

    //    TODO: should delete all consumed tokens periodically
    @Transactional
    public VerificationToken consumeToken(String verificationToken, TokenType tokenType) {
        VerificationToken token = tokenRepository.findByToken(verificationToken)
                .orElseThrow(InvalidVerificationTokenException::new);

        if (!token.getType().equals(tokenType))
            throw new InvalidVerificationTokenException();

        if (token.isUsed())
            throw new TokenValidationException(tokenType, TokenFailureReason.ALREADY_USED);

        if (token.getExpiresAt().isBefore(Instant.now()))
            throw new TokenValidationException(tokenType, TokenFailureReason.EXPIRED);

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

    @Transactional
    public void deleteAllTokensByUserId(Long userId) {
        tokenRepository.deleteAllByUserId(userId);
    }

    // #endregion

}
