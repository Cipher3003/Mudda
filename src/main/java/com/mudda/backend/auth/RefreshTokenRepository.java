/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : RefreshTokenRepository
 * Author  : Vikas Kumar
 * Created : 13-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);

    List<RefreshToken> findAllByUserId(long userId);
}
