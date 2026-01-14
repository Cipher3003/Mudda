/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : AuthService
 * Author  : Vikas Kumar
 * Created : 13-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.auth;

import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.UserService;
import com.mudda.backend.utils.MessageCodes;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenHasUtil tokenHasUtil;

    public AuthService(UserService userService,
                       JwtService jwtService,
                       RefreshTokenRepository refreshTokenRepository,
                       AuthenticationManager authenticationManager,
                       TokenHasUtil tokenHasUtil) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.authenticationManager = authenticationManager;
        this.tokenHasUtil = tokenHasUtil;
    }

    // #region Commands (Write Operations)

    @Transactional
    public AuthResult login(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));

        MuddaUser muddaUser = (MuddaUser) authentication.getPrincipal();
        return getAuthResult(muddaUser);
    }

    //    TODO: do occasional cleanup of refresh tokens
    @Transactional
    public AuthResult refresh(String rawRefreshToken) {
        if (!jwtService.validateRefreshToken(rawRefreshToken))
            throw new InvalidRefreshTokenException(MessageCodes.INVALID_REFRESH_TOKEN);

        String hashedToken = tokenHasUtil.hashToken(rawRefreshToken);

        RefreshToken savedRefreshToken = refreshTokenRepository.findByTokenAndRevokedFalse(hashedToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        savedRefreshToken.revoke();
        refreshTokenRepository.save(savedRefreshToken);

        MuddaUser user = userService.findById(savedRefreshToken.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return getAuthResult(user);
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        String hashedToken = tokenHasUtil.hashToken(rawRefreshToken);

        refreshTokenRepository.findByTokenAndRevokedFalse(hashedToken)
                .ifPresent(refreshToken -> {
                    refreshToken.revoke();
                    refreshTokenRepository.save(refreshToken);
                });
    }

    @Transactional
    protected AuthResult getAuthResult(MuddaUser muddaUser) {
        String accessToken = jwtService.generateAccessToken(muddaUser.getUsername());
        String refreshToken = jwtService.generateRefreshToken(muddaUser.getUsername());

        RefreshToken hashedRefreshToken = new RefreshToken(
                muddaUser.getUserId(),
                tokenHasUtil.hashToken(refreshToken),
                jwtService.getRefreshTokenExpirationTimeMs());
        refreshTokenRepository.save(hashedRefreshToken);

        return new AuthResult(
                accessToken, refreshToken,
                jwtService.getAccessTokenExpirationTimeMs(),
                jwtService.getRefreshTokenExpirationTimeMs(),
                muddaUser
        );
    }

    // #endregion

}
