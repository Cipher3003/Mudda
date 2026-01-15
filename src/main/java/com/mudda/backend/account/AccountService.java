/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : AccountVerificationService
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.account;

import com.mudda.backend.exceptions.UserAlreadyExistsException;
import com.mudda.backend.token.refresh.RefreshTokenService;
import com.mudda.backend.token.TokenType;
import com.mudda.backend.token.verification.VerificationToken;
import com.mudda.backend.token.verification.VerificationTokenService;
import com.mudda.backend.email.EmailService;
import com.mudda.backend.user.CreateUserRequest;
import com.mudda.backend.user.MuddaUser;
import com.mudda.backend.user.UserDetailResponse;
import com.mudda.backend.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

@Service
public class AccountService {

    private final UserService userService;
    private final VerificationTokenService tokenService;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService;

    public AccountService(UserService userService,
                          VerificationTokenService tokenService,
                          EmailService emailService,
                          RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public void register(CreateUserRequest createUserRequest) {
        Optional<MuddaUser> existingUser = userService.findByEmail(createUserRequest.email());
        if (existingUser.isPresent()) {
            MuddaUser muddaUser = existingUser.get();

            if (muddaUser.isEnabled()) throw new UserAlreadyExistsException();

            //            2 minute cooldown before sending email verification link
            if (tokenService.recentTokenExists(muddaUser.getUserId(), TokenType.EMAIL_VERIFY, Duration.ofMinutes(2)))
                return;

            tokenService.invalidateUnusedTokens(muddaUser.getUserId(), TokenType.EMAIL_VERIFY);

            VerificationToken token = tokenService.generateToken(TokenType.EMAIL_VERIFY, muddaUser.getUserId());
            emailService.sendVerificationEmail(muddaUser.getEmail(), token.getToken());
            return;
        }

        UserDetailResponse user = userService.createUser(createUserRequest);
        VerificationToken token = tokenService.generateToken(TokenType.EMAIL_VERIFY, user.userId());
        emailService.sendVerificationEmail(user.email(), token.getToken());
    }

    public void resendEmailVerificationLink(String email) {
        userService.findByEmail(email).ifPresent(user -> {
            if (user.isEnabled()) return;

//            2 minute cooldown before sending email verification link
            if (tokenService.recentTokenExists(user.getUserId(), TokenType.EMAIL_VERIFY, Duration.ofMinutes(2)))
                return;

            tokenService.invalidateUnusedTokens(user.getUserId(), TokenType.EMAIL_VERIFY);
            VerificationToken token = tokenService.generateToken(TokenType.EMAIL_VERIFY, user.getUserId());
            emailService.sendVerificationEmail(email, token.getToken());
        });
    }

    @Transactional
    public void verifyEmail(String verifyToken) {
        VerificationToken token = tokenService.consumeToken(verifyToken, TokenType.EMAIL_VERIFY);

        userService.verifyUser(token.getUserId());
    }

    public void requestPasswordReset(String email) {
        userService.findByEmail(email).ifPresent(user -> {

            //            2 minute cooldown before sending email verification link
            if (tokenService.recentTokenExists(user.getUserId(), TokenType.PASSWORD_RESET, Duration.ofMinutes(2)))
                return;

            tokenService.invalidateUnusedTokens(user.getUserId(), TokenType.PASSWORD_RESET);
            VerificationToken token = tokenService.generateToken(TokenType.PASSWORD_RESET, user.getUserId());
            emailService.sendPasswordResetEmail(email, token.getToken());
        });
    }

    @Transactional
    public void resetPassword(String verifyToken, String newPassword) {
        VerificationToken token = tokenService.consumeToken(verifyToken, TokenType.PASSWORD_RESET);

        Long userId = token.getUserId();

        userService.updatePassword(userId, newPassword);

        refreshTokenService.revokeAllByUserId(userId);
        tokenService.deleteAllTokensByUserId(userId);
    }

    @Transactional
    public void deleteAccount(Long userId) {
        refreshTokenService.revokeAllByUserId(userId);

        tokenService.deleteAllTokensByUserId(userId);

        userService.deleteUser(userId);
    }

}
