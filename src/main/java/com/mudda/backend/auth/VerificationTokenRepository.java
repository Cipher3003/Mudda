/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : VerificationTokenRepository
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    @Query(value = """
             DELETE from VerificationToken t
             WHERE t.userId = :userId AND t.type = :type
            """)
    void deleteByUserIdAndType(long userId, TokenType type);
}
