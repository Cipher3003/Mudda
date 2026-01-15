/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : JwtService
 * Author  : Vikas Kumar
 * Created : 12-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateAccessToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now()
                        .plus(Duration.ofMinutes(jwtProperties.getAccessTtlMin()))))
                .signWith(getSignKey())
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .claim("type", "REFRESH")
                .subject(username)
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now()
                        .plus(Duration.ofDays(jwtProperties.getRefreshTtlDays()))))
                .signWith(getSignKey())
                .compact();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenValid(String token) {
        return extractExpiration(token).after(new Date());
    }

    public boolean validateAccessToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && isTokenValid(token));
    }

    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String type = claims.get("type", String.class);
            return type.equals("REFRESH") && isTokenValid(token);
        } catch (Exception e) {
            return false;
        }
    }

    public long getAccessTokenExpirationSeconds() {
        return Duration.ofMinutes(jwtProperties.getAccessTtlMin()).getSeconds();
    }

    public long getRefreshTokenExpirationSeconds() {
        return Duration.ofDays(jwtProperties.getRefreshTtlDays()).getSeconds();
    }
}
