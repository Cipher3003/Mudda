/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : AccountVerificationService
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.auth;

import com.mudda.backend.user.CreateUserRequest;
import com.mudda.backend.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountService {

    private final UserService userService;
    private final VerificationTokenService tokenService;
    private final EmailService emailService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AccountService(UserService userService,
                          VerificationTokenService tokenService,
                          EmailService emailService,
                          RefreshTokenRepository refreshTokenRepository) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    //    TODO: handle psql exception at all creation time
    public void register(CreateUserRequest createUserRequest) {
        userService.createUser(createUserRequest);
    }

    public void sendEmailVerificationLink(String email) {
        userService.findByEmail(email).ifPresent(user -> {
            VerificationToken token = tokenService.generateToken(TokenType.EMAIL_VERIFY, user.getUserId());
            emailService.sendVerificationEmail(email, token.getToken());
        });
    }

    public void verifyEmail(String verifyToken) {
        VerificationToken token = tokenService.consumeToken(verifyToken, TokenType.EMAIL_VERIFY);

        userService.verifyUser(token.getUserId());
    }

    public void requestPasswordReset(String email) {
    }

    public void resetPassword(String token, String newPassword) {
    }

    @Transactional
    public void deleteAccount(Long userId) {
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByUserId(userId);
        for (RefreshToken refreshToken : refreshTokens) {
            refreshToken.revoke();
        }
        refreshTokenRepository.saveAll(refreshTokens);

        tokenService.deleteAllTokensByUserId(userId);

        userService.deleteUser(userId);
    }

//    TODO: add account deletion
}
