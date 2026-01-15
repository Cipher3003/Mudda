/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : AuthService
 * Author  : Vikas Kumar
 * Created : 13-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.auth;

import com.mudda.backend.exceptions.InvalidRefreshTokenException;
import com.mudda.backend.jwt.JwtService;
import com.mudda.backend.token.refresh.RefreshToken;
import com.mudda.backend.token.refresh.RefreshTokenService;
import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserService userService,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService,
                       AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
    }

    // #region Commands (Write Operations)

    @Transactional
    public AuthResult login(AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));

            MuddaUser muddaUser = (MuddaUser) authentication.getPrincipal();
            // success side-effects
            userService.resetLoginFailures(muddaUser.getUserId());

            return getAuthResult(muddaUser, Instant.now().plusSeconds(jwtService.getRefreshTokenExpirationSeconds()));

        } catch (BadCredentialsException e) {
            // failure side-effects
            userService.recordFailedLogin(authRequest.username());

            throw e;
        }
    }

    //    TODO: do occasional cleanup of refresh tokens
    @Transactional
    public AuthResult refresh(String rawRefreshToken) {
        if (!jwtService.validateRefreshToken(rawRefreshToken))
            throw new InvalidRefreshTokenException();

        RefreshToken refreshToken = refreshTokenService.rotate(rawRefreshToken);

        MuddaUser user = userService.findById(refreshToken.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return getAuthResult(user, refreshToken.getExpiresAt());
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        refreshTokenService.revoke(rawRefreshToken);
    }

    @Transactional
    protected AuthResult getAuthResult(MuddaUser muddaUser, Instant expiresAt) {
        String accessToken = jwtService.generateAccessToken(muddaUser.getUsername());
        String refreshToken = jwtService.generateRefreshToken(muddaUser.getUsername());

        refreshTokenService.create(
                muddaUser.getUserId(),
                refreshToken,
                expiresAt);

        return new AuthResult(
                accessToken, refreshToken,
                jwtService.getAccessTokenExpirationSeconds(),
                muddaUser
        );
    }

    // #endregion

}
