/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : VerificationTokenRepository
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.token.verification;

import com.mudda.backend.token.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    @Modifying(clearAutomatically = true)
    @Query(value = """
             DELETE from VerificationToken t
             WHERE t.userId = :userId AND t.type = :type
            """)
    void deleteByUserIdAndType(long userId, TokenType type);

    void deleteAllByUserId(long userId);

    @Modifying(clearAutomatically = true)
    @Query(value = """
             UPDATE VerificationToken t
             SET t.usedAt = CURRENT_TIMESTAMP
             WHERE t.id = :id
            """
    )
    void markUsed(Long id);

    @Modifying(clearAutomatically = true)
    @Query(value = """
             UPDATE VerificationToken t
             SET t.usedAt = CURRENT_TIMESTAMP
             WHERE t.userId = :userId AND t.type = :type AND t.usedAt IS null
            """)
    void invalidateUnusedTokens(long userId, TokenType type);

    boolean existsByUserIdAndTypeAndCreatedAtAfter(long userId, TokenType tokenType, Instant minus);
}
