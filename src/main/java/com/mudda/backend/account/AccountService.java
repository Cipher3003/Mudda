/**
 * ---------------------------------------------------------------
 * Project : Mudda
 * File    : AccountVerificationService
 * Author  : Vikas Kumar
 * Created : 14-01-2026
 * ---------------------------------------------------------------
 */
package com.mudda.backend.account;

import com.mudda.backend.AppProperties;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
public class AccountService {

    private final UserService userService;
    private final VerificationTokenService tokenService;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService;
    private final AppProperties appProperties;

    public AccountService(UserService userService,
                          VerificationTokenService tokenService,
                          EmailService emailService,
                          RefreshTokenService refreshTokenService,
                          AppProperties appProperties) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.refreshTokenService = refreshTokenService;
        this.appProperties = appProperties;
    }

    //region Commands (Write Operations)

    @Transactional
    public void register(CreateUserRequest createUserRequest) {
        log.info("Registering new user account");
        Optional<MuddaUser> existingUser = userService.findByEmail(createUserRequest.email());

        if (existingUser.isPresent()) {
            log.trace("User with email {} already exists", createUserRequest.email());
            MuddaUser muddaUser = existingUser.get();

            if (muddaUser.isEnabled()) {
                log.warn("User with email {} already exists and verified", createUserRequest.email());
                throw new UserAlreadyExistsException();
            }

            tokenService.invalidateUnusedTokens(muddaUser.getUserId(), TokenType.EMAIL_VERIFY);

            log.trace("Sending verification token to user {}", muddaUser.getEmail());
            VerificationToken token = tokenService.generateToken(TokenType.EMAIL_VERIFY, muddaUser.getUserId());
            emailService.sendVerificationEmail(muddaUser.getEmail(), token.getToken());
            return;
        }

        log.trace("Creating new user account");
        UserDetailResponse user = userService.createUser(createUserRequest);

        log.trace("Sending verification token to user {}", user.email());
        VerificationToken token = tokenService.generateToken(TokenType.EMAIL_VERIFY, user.userId());
        emailService.sendVerificationEmail(user.email(), token.getToken());
    }

    public void resendEmailVerificationLink(String email) {
        log.info("Resending email verification link to {}", email);
        userService.findByEmail(email).ifPresent(user -> {
            if (user.isEnabled()) {
                log.warn("User with email {} already verified", email);
                return;
            }

            if (tokenService.recentTokenExists(user.getUserId(), TokenType.EMAIL_VERIFY, resendCooldown()))
                return;

            tokenService.invalidateUnusedTokens(user.getUserId(), TokenType.EMAIL_VERIFY);

            log.trace("Sending verification token to user {}", email);
            VerificationToken token = tokenService.generateToken(TokenType.EMAIL_VERIFY, user.getUserId());
            emailService.sendVerificationEmail(email, token.getToken());
        });
    }

    @Transactional
    public void verifyEmail(String verifyToken) {
        log.info("Verifying email verification token {}", verifyToken);
        VerificationToken token = tokenService.consumeToken(verifyToken, TokenType.EMAIL_VERIFY);

        userService.verifyUser(token.getUserId());
    }

    @Transactional
    public void requestPasswordReset(String email) {
        log.info("Generating password reset link for user {}", email);
        userService.findByEmail(email).ifPresent(user -> {

            if (tokenService.recentTokenExists(user.getUserId(), TokenType.PASSWORD_RESET, resendCooldown()))
                return;

            tokenService.invalidateUnusedTokens(user.getUserId(), TokenType.PASSWORD_RESET);

            log.trace("Sending password reset token to user {}", email);
            VerificationToken token = tokenService.generateToken(TokenType.PASSWORD_RESET, user.getUserId());
            emailService.sendPasswordResetEmail(email, token.getToken());
        });
    }

    @Transactional
    public void resetPassword(String verifyToken, String newPassword) {
        VerificationToken token = tokenService.consumeToken(verifyToken, TokenType.PASSWORD_RESET);

        Long userId = token.getUserId();
        log.info("Resetting password for user with ID:{}", userId);

        userService.updatePassword(userId, newPassword);

        refreshTokenService.revokeAllByUserId(userId);
        tokenService.deleteAllTokensByUserId(userId);
    }

    @Transactional
    public void deleteAccount(Long userId) {
        log.info("Deleting account with id {}", userId);

        refreshTokenService.revokeAllByUserId(userId);

        tokenService.deleteAllTokensByUserId(userId);

        userService.deleteUser(userId);
        log.trace("Deleted account with id {}", userId);
    }

    //endregion

    private Duration resendCooldown() {
        return Duration.ofMinutes(appProperties.getToken().getResendCooldownMinutes());
    }

}
